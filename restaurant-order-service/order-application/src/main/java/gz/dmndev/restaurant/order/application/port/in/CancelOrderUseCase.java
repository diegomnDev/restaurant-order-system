package gz.dmndev.restaurant.order.application.port.in;

import gz.dmndev.restaurant.order.domain.model.Order;

public interface CancelOrderUseCase {
  Order cancelOrder(String id);
}
