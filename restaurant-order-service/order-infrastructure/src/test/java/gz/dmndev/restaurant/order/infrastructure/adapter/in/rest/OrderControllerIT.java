package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(OrderController.class)
@ContextConfiguration(
    classes = {
      OrderController.class,
    })
class OrderControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateOrderUseCase createOrderUseCase;

  @MockitoBean private GetOrderUseCase getOrderUseCase;

  @MockitoBean private UpdateOrderStatusUseCase updateOrderStatusUseCase;

  @MockitoBean private CancelOrderUseCase cancelOrderUseCase;

  @MockitoBean private OrderApiMapper mapper;

  private Order testOrder;
  private OrderResponse testOrderResponse;

  @BeforeEach
  void setUp() {
    // Usar el método de fábrica para crear el item
    OrderItem item =
        OrderItem.createNew("item-1", "prod-1", "Test Product", 2, new BigDecimal("10.00"));

    // Crear la orden usando el builder
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

    // Crear la respuesta usando getters
    testOrderResponse =
        new OrderResponse(
            testOrder.getId(),
            testOrder.getCustomerId(),
            testOrder.getCustomerName(),
            null,
            testOrder.getSubtotal(),
            testOrder.getTax(),
            testOrder.getTotal(),
            testOrder.getStatus(),
            testOrder.getCreatedAt(),
            testOrder.getUpdatedAt(),
            testOrder.getNotes());
  }

  @Test
  void createOrder_shouldReturn201AndOrderResponse() throws Exception {
    // Arrange
    CreateOrderRequest request =
        new CreateOrderRequest(
            "cust-1", "Test Customer", List.of(new OrderItemRequest("prod-1", 2)), "Test order");

    when(createOrderUseCase.createOrder(any(), any(), any(), any())).thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("order-1"))
        .andExpect(jsonPath("$.customerId").value("cust-1"))
        .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getOrderById_shouldReturn200AndOrder_whenOrderExists() throws Exception {
    // Arrange
    when(getOrderUseCase.getOrderById("order-1")).thenReturn(Optional.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(get("/orders/order-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("order-1"))
        .andExpect(jsonPath("$.customerId").value("cust-1"));
  }

  @Test
  void getOrderById_shouldReturn404_whenOrderNotExists() throws Exception {
    // Arrange
    when(getOrderUseCase.getOrderById("non-existent")).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/orders/non-existent")).andExpect(status().isNotFound());
  }

  @Test
  void getAllOrders_shouldReturn200AndOrderList() throws Exception {
    // Arrange
    when(getOrderUseCase.getAllOrders()).thenReturn(List.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(get("/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("order-1"))
        .andExpect(jsonPath("$[0].customerId").value("cust-1"));
  }

  @Test
  void getOrdersByCustomerId_shouldReturn200AndOrderList() throws Exception {
    // Arrange
    when(getOrderUseCase.getOrdersByCustomerId("cust-1")).thenReturn(List.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(get("/orders/customer/cust-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("order-1"))
        .andExpect(jsonPath("$[0].customerId").value("cust-1"));
  }

  @Test
  void getOrdersByStatus_shouldReturn200AndOrderList() throws Exception {
    // Arrange
    when(getOrderUseCase.getOrdersByStatus(OrderStatus.CREATED)).thenReturn(List.of(testOrder));
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(get("/orders/status/CREATED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("order-1"))
        .andExpect(jsonPath("$[0].status").value("CREATED"));
  }

  @Test
  void updateOrderStatus_shouldReturn200AndUpdatedOrder() throws Exception {
    // Arrange
    UpdateStatusRequest request = new UpdateStatusRequest(OrderStatus.PAID);

    when(updateOrderStatusUseCase.updateOrderStatus("order-1", OrderStatus.PAID))
        .thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(
            put("/orders/order-1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("order-1"))
        .andExpect(jsonPath("$.status").value("CREATED")); // Asume que el mock no cambia el estado
  }

  @Test
  void cancelOrder_shouldReturn200AndCancelledOrder() throws Exception {
    // Arrange
    when(cancelOrderUseCase.cancelOrder("order-1")).thenReturn(testOrder);
    when(mapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);

    // Act & Assert
    mockMvc
        .perform(put("/orders/order-1/cancel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("order-1"));
  }
}
