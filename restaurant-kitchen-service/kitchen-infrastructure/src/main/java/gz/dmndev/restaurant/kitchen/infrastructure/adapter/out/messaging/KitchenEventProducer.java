package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.messaging;

import gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.messaging.dto.OrderPreparedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Component for publishing kitchen events to Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KitchenEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kitchen.kafka.topics.kitchen-events}")
    private String kitchenEventsTopic;

    @Value("${kitchen.kafka.topics.order-events}")
    private String orderEventsTopic;

    /**
     * Publish a kitchen event
     *
     * @param topic the Kafka topic
     * @param key the message key
     * @param event the event object
     * @return a CompletableFuture with the send result
     */
    public <T> CompletableFuture<SendResult<String, String>> publishEvent(String topic, String key, T event) {
        String eventJson;
        try {
            eventJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }

        log.info("Publishing event to topic {}: {}", topic, key);
        log.debug("Event payload: {}", eventJson);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, eventJson);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published successfully to topic {}: {}", topic, key);
                log.debug("Partition: {}, Offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage());
            }
        });

        return future;
    }

    /**
     * Publish an order prepared event
     *
     * @param event the order prepared event
     * @return a CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, String>> publishOrderPreparedEvent(OrderPreparedEvent event) {
        // Set the event ID if not already set
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        // Publish to both topics for different consumers
        CompletableFuture<SendResult<String, String>> kitchenResult =
                publishEvent(kitchenEventsTopic, event.getOrderId(), event);

        CompletableFuture<SendResult<String, String>> orderResult =
                publishEvent(orderEventsTopic, event.getOrderId(), event);

        // Return the kitchen result for consistency
        return kitchenResult;
    }

    /**
     * Publish an order cancelled event
     *
     * @param orderId the ID of the cancelled order
     * @param reason the cancellation reason
     * @return a CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, String>> publishOrderCancelledEvent(String orderId, String reason) {
        var event = new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                orderId,
                reason
        );

        return publishEvent(orderEventsTopic, orderId, event);
    }

    /**
     * Simple DTO for order cancelled events
     */
    record OrderCancelledEvent(String eventId, String orderId, String reason) { }
}