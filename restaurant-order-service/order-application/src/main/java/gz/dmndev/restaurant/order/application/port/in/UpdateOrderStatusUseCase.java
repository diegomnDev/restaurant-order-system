package gz.dmndev.restaurant.order.application.port.in;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;

public interface UpdateOrderStatusUseCase {
  Order updateOrderStatus(String id, OrderStatus status);
}
