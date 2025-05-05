package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.common.messaging.constants.KafkaTopics;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {TestKafkaConfig.class})
@EmbeddedKafka(
    topics = {KafkaTopics.ORDER_EVENTS},
    brokerProperties = {
      "listeners=PLAINTEXT://localhost:0",
      "port=0",
      "auto.create.topics.enable=true"
    })
@Import(TestKafkaConfig.class)
@ActiveProfiles("test")
@DirtiesContext
class OrderEventPublisherAdapterIT {

  @Autowired private EmbeddedKafkaBroker embeddedKafka;

  @Autowired private OrderEventPublisherAdapter adapter;

  private Consumer<String, OrderEventDto> consumer;

  @BeforeEach
  void setUp() {
    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testGroup-" + UUID.randomUUID(), "true", embeddedKafka);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEventDto.class);

    consumer =
        new DefaultKafkaConsumerFactory<String, OrderEventDto>(consumerProps).createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, KafkaTopics.ORDER_EVENTS);
  }

  @AfterEach
  void tearDown() {
    if (consumer != null) {
      consumer.close();
    }
  }

  @Test
  void publishOrderCreatedEvent_shouldPublishEventWithCorrectData() {
    Order order = createTestOrder(OrderStatus.CREATED);

    adapter.publishOrderCreatedEvent(order);

    assertPublishedEvent(order, "ORDER_CREATED");
  }

  @Test
  void publishOrderUpdatedEvent_shouldPublishEventWithCorrectData() {
    Order order = createTestOrder(OrderStatus.PREPARING);

    adapter.publishOrderUpdatedEvent(order);

    assertPublishedEvent(order, "ORDER_UPDATED");
  }

  @Test
  void publishOrderCancelledEvent_shouldPublishEventWithCorrectData() {
    Order order = createTestOrder(OrderStatus.CANCELLED);

    adapter.publishOrderCancelledEvent(order);

    assertPublishedEvent(order, "ORDER_CANCELLED");
  }

  private void assertPublishedEvent(Order order, String expectedEventType) {
    ConsumerRecords<String, OrderEventDto> records =
        KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5));

    // Filter records to only include those with our order ID
    List<ConsumerRecord<String, OrderEventDto>> matchingRecords =
        StreamSupport.stream(records.spliterator(), false)
            .filter(r -> order.getId().equals(r.key()))
            .toList();

    assertEquals(1, matchingRecords.size(), "Expected exactly one matching record");

    ConsumerRecord<String, OrderEventDto> record = matchingRecords.get(0);
    OrderEventDto event = record.value();
    assertEquals(expectedEventType, event.getEventType());
    assertEquals(order.getId(), event.getOrderId());
    assertEquals(order.getStatus().name(), event.getStatus().name());
    assertNotNull(event.getTimestamp());
  }

  private Order createTestOrder(OrderStatus status) {
    OrderItem item =
        OrderItem.builder()
            .id(UUID.randomUUID().toString())
            .productId("prod-" + UUID.randomUUID())
            .productName("Test Product")
            .quantity(2)
            .unitPrice(new BigDecimal("10.00"))
            .totalPrice(new BigDecimal("20.00"))
            .build();

    return Order.builder()
        .id(UUID.randomUUID().toString())
        .customerId("cust-" + UUID.randomUUID())
        .items(List.of(item))
        .status(status)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
