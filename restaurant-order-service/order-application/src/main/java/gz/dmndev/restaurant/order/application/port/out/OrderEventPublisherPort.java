package gz.dmndev.restaurant.order.application.port.out;

import gz.dmndev.restaurant.order.domain.model.Order;

public interface OrderEventPublisherPort {
  void publishOrderCreatedEvent(Order order);

  void publishOrderUpdatedEvent(Order order);

  void publishOrderCancelledEvent(Order order);
}
