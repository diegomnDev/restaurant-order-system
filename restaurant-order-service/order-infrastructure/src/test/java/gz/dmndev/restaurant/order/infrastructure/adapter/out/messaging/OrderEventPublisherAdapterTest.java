package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging;

import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.common.messaging.constants.KafkaTopics;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.mapper.OrderEventMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherAdapterTest {

  @Mock private KafkaTemplate<String, OrderEventDto> kafkaTemplate;

  @Mock private OrderEventMapper mapper;

  @InjectMocks private OrderEventPublisherAdapter adapter;

  private Order testOrder;
  private OrderEventDto testEventDto;

  @BeforeEach
  void setUp() {
    OrderItem item =
        OrderItem.builder()
            .id("item-1")
            .productId("prod-1")
            .productName("Test Product")
            .quantity(2)
            .unitPrice(new BigDecimal("10.00"))
            .totalPrice(new BigDecimal("20.00"))
            .build();

    testOrder =
        Order.builder()
            .id("order-1")
            .customerId("cust-1")
            .customerName("Test Customer")
            .items(List.of(item))
            .subtotal(new BigDecimal("20.00"))
            .tax(new BigDecimal("1.60"))
            .total(new BigDecimal("21.60"))
            .status(OrderStatus.CREATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .notes("Test order")
            .build();

    testEventDto = mock(OrderEventDto.class);
  }

  @Test
  void publishOrderCreatedEvent_shouldSendToKafka() {
    // Arrange
    when(mapper.toOrderCreatedEventDto(testOrder)).thenReturn(testEventDto);
    when(kafkaTemplate.send(anyString(), anyString(), any(OrderEventDto.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    adapter.publishOrderCreatedEvent(testOrder);

    // Assert
    verify(mapper).toOrderCreatedEventDto(testOrder);
    verify(kafkaTemplate).send(KafkaTopics.ORDER_EVENTS, testOrder.getId(), testEventDto);
  }

  @Test
  void publishOrderUpdatedEvent_shouldSendToKafka() {
    // Arrange
    when(mapper.toOrderUpdatedEventDto(testOrder)).thenReturn(testEventDto);
    when(kafkaTemplate.send(anyString(), anyString(), any(OrderEventDto.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    adapter.publishOrderUpdatedEvent(testOrder);

    // Assert
    verify(mapper).toOrderUpdatedEventDto(testOrder);
    verify(kafkaTemplate).send(KafkaTopics.ORDER_EVENTS, testOrder.getId(), testEventDto);
  }

  @Test
  void publishOrderCancelledEvent_shouldSendToKafka() {
    // Arrange
    when(mapper.toOrderCancelledEventDto(testOrder)).thenReturn(testEventDto);
    when(kafkaTemplate.send(anyString(), anyString(), any(OrderEventDto.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    adapter.publishOrderCancelledEvent(testOrder);

    // Assert
    verify(mapper).toOrderCancelledEventDto(testOrder);
    verify(kafkaTemplate).send(KafkaTopics.ORDER_EVENTS, testOrder.getId(), testEventDto);
  }
}
