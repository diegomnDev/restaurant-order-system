package gz.dmndev.restaurant.order.domain.repository;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
  Order save(Order order);

  Optional<Order> findById(String id);

  List<Order> findByCustomerId(String customerId);

  List<Order> findByStatus(OrderStatus status);

  List<Order> findAll();

  void deleteById(String id);
}
