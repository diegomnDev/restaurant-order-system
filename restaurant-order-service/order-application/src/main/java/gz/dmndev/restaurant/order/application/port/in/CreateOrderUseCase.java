package gz.dmndev.restaurant.order.application.port.in;

import gz.dmndev.restaurant.order.domain.model.Order;
import java.util.List;

public interface CreateOrderUseCase {
  Order createOrder(
      String customerId, String customerName, List<OrderItemCommand> items, String notes);

  record OrderItemCommand(String productId, int quantity) {}
}
