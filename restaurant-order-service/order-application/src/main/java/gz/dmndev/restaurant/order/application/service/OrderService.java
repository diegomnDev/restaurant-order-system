package gz.dmndev.restaurant.order.application.service;

import gz.dmndev.restaurant.order.application.port.in.*;
import gz.dmndev.restaurant.order.application.port.out.MenuServicePort;
import gz.dmndev.restaurant.order.application.port.out.OrderEventPublisherPort;
import gz.dmndev.restaurant.order.application.port.out.OrderRepositoryPort;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService
    implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase, CancelOrderUseCase {

  private final OrderRepositoryPort orderRepository;
  private final MenuServicePort menuService;
  private final OrderEventPublisherPort eventPublisher;

  @Override
  @Transactional
  public Order createOrder(
      String customerId, String customerName, List<OrderItemCommand> items, String notes) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Order must contain at least one item");
    }

    Order order = Order.createNew(UUID.randomUUID().toString(), customerId, customerName);

    order.updateNotes(notes);

    for (OrderItemCommand itemCommand : items) {
      MenuServicePort.ProductInfo productInfo =
          menuService
              .getProduct(itemCommand.productId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Product not found: " + itemCommand.productId()));

      if (!productInfo.available()) {
        throw new IllegalArgumentException("Product is not available: " + productInfo.name());
      }

      OrderItem orderItem =
          OrderItem.createNew(
              UUID.randomUUID().toString(),
              productInfo.id(),
              productInfo.name(),
              itemCommand.quantity(),
              productInfo.price());

      order.addItem(orderItem);
    }

    Order savedOrder = orderRepository.save(order);

    eventPublisher.publishOrderCreatedEvent(savedOrder);

    return savedOrder;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getOrderById(String id) {
    return orderRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByCustomerId(String customerId) {
    return orderRepository.findByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getOrdersByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  @Override
  @Transactional
  public Order updateOrderStatus(String id, OrderStatus status) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

    order.getStatus().validateTransition(status);

    order.updateStatus(status);

    Order savedOrder = orderRepository.save(order);

    eventPublisher.publishOrderUpdatedEvent(savedOrder);

    return savedOrder;
  }

  @Override
  @Transactional
  public Order cancelOrder(String id) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

    // Only orders in CREATED or PAID status can be cancelled
    order.getStatus().validateTransition(OrderStatus.CANCELLED);

    order.updateStatus(OrderStatus.CANCELLED);

    Order savedOrder = orderRepository.save(order);

    eventPublisher.publishOrderCancelledEvent(savedOrder);

    return savedOrder;
  }
}
