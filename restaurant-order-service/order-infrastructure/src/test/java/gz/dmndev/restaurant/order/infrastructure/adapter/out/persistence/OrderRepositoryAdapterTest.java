package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

  @Mock private OrderJpaRepository orderRepository;

  @Mock private OrderPersistenceMapper mapper;

  @InjectMocks private OrderRepositoryAdapter adapter;

  private Order testOrder;
  private OrderEntity testEntity;
  private final String orderId = "test-order-1";

  @BeforeEach
  void setUp() {
    OrderItem item =
        OrderItem.createNew("item-1", "prod-1", "Test Product", 2, new BigDecimal("10.00"));

    testOrder =
        Order.builder()
            .id(orderId)
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

    testEntity = new OrderEntity();
    testEntity.setId(orderId);
  }

  @Test
  void save_shouldReturnSavedOrder() {
    // Arrange
    when(mapper.toEntityWithRelationships(testOrder)).thenReturn(testEntity);
    when(orderRepository.save(testEntity)).thenReturn(testEntity);
    when(mapper.toDomainWithRelationships(testEntity)).thenReturn(testOrder);

    // Act
    Order result = adapter.save(testOrder);

    // Assert
    assertEquals(testOrder, result);
    verify(mapper).toEntityWithRelationships(testOrder);
    verify(orderRepository).save(testEntity);
    verify(mapper).toDomainWithRelationships(testEntity);
  }

  @Test
  void findById_shouldReturnOrder_whenFound() {
    // Arrange
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(testEntity));
    when(mapper.toDomainWithRelationships(testEntity)).thenReturn(testOrder);

    // Act
    Optional<Order> result = adapter.findById(orderId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(testOrder, result.get());
  }

  @Test
  void findById_shouldReturnEmpty_whenNotFound() {
    // Arrange
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // Act
    Optional<Order> result = adapter.findById(orderId);

    // Assert
    assertTrue(result.isEmpty());
    verify(mapper, never()).toDomainWithRelationships(any());
  }

  @Test
  void findByCustomerId_shouldReturnOrderList() {
    // Arrange
    String customerId = "cust-1";
    when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of(testEntity));
    when(mapper.toDomainWithRelationships(testEntity)).thenReturn(testOrder);

    // Act
    List<Order> result = adapter.findByCustomerId(customerId);

    // Assert
    assertEquals(1, result.size());
    assertEquals(testOrder, result.get(0));
  }

  @Test
  void findByStatus_shouldReturnOrderList() {
    // Arrange
    OrderStatus status = OrderStatus.CREATED;
    when(orderRepository.findByStatus(status)).thenReturn(List.of(testEntity));
    when(mapper.toDomainWithRelationships(testEntity)).thenReturn(testOrder);

    // Act
    List<Order> result = adapter.findByStatus(status);

    // Assert
    assertEquals(1, result.size());
    assertEquals(testOrder, result.get(0));
  }

  @Test
  void findAll_shouldReturnAllOrders() {
    // Arrange
    when(orderRepository.findAll()).thenReturn(List.of(testEntity));
    when(mapper.toDomainWithRelationships(testEntity)).thenReturn(testOrder);

    // Act
    List<Order> result = adapter.findAll();

    // Assert
    assertEquals(1, result.size());
    assertEquals(testOrder, result.get(0));
  }

  @Test
  void findByCustomerId_shouldReturnEmptyList_whenNoOrdersFound() {
    // Arrange
    String customerId = "non-existent";
    when(orderRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

    // Act
    List<Order> result = adapter.findByCustomerId(customerId);

    // Assert
    assertTrue(result.isEmpty());
    verify(mapper, never()).toDomainWithRelationships(any());
  }
}
