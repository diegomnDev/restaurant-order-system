// En
// order-infrastructure/src/main/java/gz/dmndev/restaurant/order/infrastructure/adapter/in/rest/OrderController.java
package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest;

import gz.dmndev.restaurant.order.application.port.in.*;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.UpdateStatusRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.mapper.OrderApiMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final CreateOrderUseCase createOrderUseCase;
  private final GetOrderUseCase getOrderUseCase;
  private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
  private final CancelOrderUseCase cancelOrderUseCase;
  private final OrderApiMapper mapper;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    Order order =
        createOrderUseCase.createOrder(
            request.customerId(),
            request.customerName(),
            mapper.toOrderItemCommands(request.items()),
            request.notes());
    return new ResponseEntity<>(mapper.toOrderResponse(order), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
    return getOrderUseCase
        .getOrderById(id)
        .map(order -> new ResponseEntity<>(mapper.toOrderResponse(order), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getAllOrders() {
    List<OrderResponse> orders =
        getOrderUseCase.getAllOrders().stream()
            .map(mapper::toOrderResponse)
            .collect(Collectors.toList());
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(
      @PathVariable String customerId) {
    List<OrderResponse> orders =
        getOrderUseCase.getOrdersByCustomerId(customerId).stream()
            .map(mapper::toOrderResponse)
            .collect(Collectors.toList());
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
    List<OrderResponse> orders =
        getOrderUseCase.getOrdersByStatus(status).stream()
            .map(mapper::toOrderResponse)
            .collect(Collectors.toList());
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
    Order order = updateOrderStatusUseCase.updateOrderStatus(id, request.status());
    return new ResponseEntity<>(mapper.toOrderResponse(order), HttpStatus.OK);
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String id) {
    Order order = cancelOrderUseCase.cancelOrder(id);
    return new ResponseEntity<>(mapper.toOrderResponse(order), HttpStatus.OK);
  }
}
