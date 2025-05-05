package gz.dmndev.restaurant.order.application.port.in;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface GetOrderUseCase {
  Optional<Order> getOrderById(String id);

  List<Order> getOrdersByCustomerId(String customerId);

  List<Order> getOrdersByStatus(OrderStatus status);

  List<Order> getAllOrders();
}
