package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.order.application.port.in.*;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderItemRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.UpdateStatusRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.mapper.OrderApiMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock private CreateOrderUseCase createOrderUseCase;

  @Mock private GetOrderUseCase getOrderUseCase;

  @Mock private UpdateOrderStatusUseCase updateOrderStatusUseCase;

  @Mock private CancelOrderUseCase cancelOrderUseCase;

  @Mock private OrderApiMapper mapper;

  @InjectMocks private OrderController controller;

  private Order testOrder;
  private OrderResponse testOrderResponse;
  private CreateOrderRequest createRequest;
  private List<CreateOrderUseCase.OrderItemCommand> orderItemCommands;

  @BeforeEach
  void setUp() {
    OrderItem item =
        OrderItem.createNew("item-1", "prod-1", "Test Product", 2, new BigDecimal("10.00"));

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

    testOrderResponse =
        new OrderResponse(
            testOrder.getId(),
            testOrder.getCustomerId(),
            testOrder.getCustomerName(),
            null, // Mapper will handle this
            testOrder.getSubtotal(),
            testOrder.getTax(),
            testOrder.getTotal(),
            testOrder.getStatus(),
            testOrder.getCreatedAt(),
            testOrder.getUpdatedAt(),
            testOrder.getNotes());

    createRequest =
        new CreateOrderRequest(
            "cust-1", "Test Customer", List.of(new OrderItemRequest("prod-1", 2)), "Test order");

    orderItemCommands = List.of(new CreateOrderUseCase.OrderItemCommand("prod-1", 2));
  }

  @Test
  void createOrder_shouldReturnCreatedOrder() {
    // Arrange
    when(mapper.toOrderItemCommands(anyList())).thenReturn(orderItemCommands);
    when(createOrderUseCase.createOrder(anyString(), anyString(), anyList(), anyString()))
        .thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act
    ResponseEntity<OrderResponse> response = controller.createOrder(createRequest);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testOrderResponse, response.getBody());
    verify(createOrderUseCase)
        .createOrder(
            createRequest.customerId(),
            createRequest.customerName(),
            orderItemCommands,
            createRequest.notes());
  }

  @Test
  void getOrderById_shouldReturnOrder_whenFound() {
    // Arrange
    String orderId = "order-1";
    when(getOrderUseCase.getOrderById(orderId)).thenReturn(Optional.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act
    ResponseEntity<OrderResponse> response = controller.getOrderById(orderId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testOrderResponse, response.getBody());
  }

  @Test
  void getOrderById_shouldReturnNotFound_whenOrderDoesNotExist() {
    // Arrange
    String orderId = "non-existent";
    when(getOrderUseCase.getOrderById(orderId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<OrderResponse> response = controller.getOrderById(orderId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void getAllOrders_shouldReturnAllOrders() {
    // Arrange
    when(getOrderUseCase.getAllOrders()).thenReturn(List.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act
    ResponseEntity<List<OrderResponse>> response = controller.getAllOrders();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals(testOrderResponse, response.getBody().get(0));
  }

  @Test
  void updateOrderStatus_shouldReturnUpdatedOrder() {
    // Arrange
    String orderId = "order-1";
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.PAID);

    when(updateOrderStatusUseCase.updateOrderStatus(orderId, OrderStatus.PAID))
        .thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act
    ResponseEntity<OrderResponse> response = controller.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testOrderResponse, response.getBody());
    verify(updateOrderStatusUseCase).updateOrderStatus(orderId, OrderStatus.PAID);
  }

  @Test
  void cancelOrder_shouldReturnCancelledOrder() {
    // Arrange
    String orderId = "order-1";
    when(cancelOrderUseCase.cancelOrder(orderId)).thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act
    ResponseEntity<OrderResponse> response = controller.cancelOrder(orderId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testOrderResponse, response.getBody());
    verify(cancelOrderUseCase).cancelOrder(orderId);
  }
}
