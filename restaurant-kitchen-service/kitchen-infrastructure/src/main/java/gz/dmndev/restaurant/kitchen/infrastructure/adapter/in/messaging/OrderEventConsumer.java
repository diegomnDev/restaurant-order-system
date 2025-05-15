package gz.dmndev.restaurant.kitchen.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gz.dmndev.restaurant.kitchen.application.port.in.CreateTicketUseCase;
import gz.dmndev.restaurant.kitchen.application.port.in.GetKitchenTicketUseCase;
import gz.dmndev.restaurant.kitchen.application.port.in.UpdateTicketStatusUseCase;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.infrastructure.adapter.in.messaging.event.OrderCreatedEvent;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for order events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final CreateTicketUseCase createTicketUseCase;
    private final UpdateTicketStatusUseCase updateTicketStatusUseCase;
    private final GetKitchenTicketUseCase getKitchenTicketUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Handle order-created events
     *
     * @param payload the event payload
     */
    @KafkaListener(
            topics = "${kitchen.kafka.topics.order-events}",
            groupId = "${kitchen.kafka.consumer.group-id}",
            filter = "orderCreatedFilter"
    )
    public void handleOrderCreatedEvent(@Payload String payload) {
        log.info("Received order-created event");
        log.debug("Event payload: {}", payload);

        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

            log.info("Processing order-created event for order ID: {}", event.getOrderId());

            // Map event to create ticket command
            var ticketItems = event.getItems().stream()
                    .map(item -> new CreateTicketUseCase.CreateTicketItemCommand(
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getSpecialInstructions()
                    ))
                    .collect(Collectors.toList());

            var command = new CreateTicketUseCase.CreateTicketCommand(
                    event.getOrderId(),
                    event.getCustomerId(),
                    event.getCustomerName(),
                    ticketItems,
                    event.getNotes(),
                    event.getPriority()
            );

            // Create kitchen ticket
            String ticketId = createTicketUseCase.createTicket(command);

            log.info("Created kitchen ticket {} for order {}", ticketId, event.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("Error deserializing order-created event: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing order-created event: {}", e.getMessage());
        }
    }

    /**
     * Handle order-cancelled events
     *
     * @param payload the event payload
     */
    @KafkaListener(
            topics = "${kitchen.kafka.topics.order-events}",
            groupId = "${kitchen.kafka.consumer.group-id}",
            filter = "orderCancelledFilter"
    )
    public void handleOrderCancelledEvent(@Payload String payload) {
        log.info("Received order-cancelled event");
        log.debug("Event payload: {}", payload);

        try {
            // Extract order ID and event type from the payload
            var eventNode = objectMapper.readTree(payload);
            var eventType = eventNode.path("eventType").asText();

            if (!"ORDER_CANCELLED".equals(eventType)) {
                log.debug("Ignoring non-cancellation event: {}", eventType);
                return;
            }

            var orderId = eventNode.path("orderId").asText();

            log.info("Processing order-cancelled event for order ID: {}", orderId);

            // Find ticket by order ID and cancel it
            getKitchenTicketUseCase.getTicketByOrderId(orderId)
                    .map(ticket -> updateTicketStatusUseCase.updateTicketStatus(
                            new UpdateTicketStatusUseCase.UpdateTicketStatusCommand(
                                    ticket.id(),
                                    PrepStatus.CANCELLED,
                                    null
                            )
                    ))
                    .ifPresentOrElse(
                            result -> log.info("Cancelled kitchen ticket for order {}: {}", orderId, result),
                            () -> log.info("No kitchen ticket found for order {}", orderId)
                    );
        } catch (Exception e) {
            log.error("Error processing order-cancelled event: {}", e.getMessage());
        }
    }
}