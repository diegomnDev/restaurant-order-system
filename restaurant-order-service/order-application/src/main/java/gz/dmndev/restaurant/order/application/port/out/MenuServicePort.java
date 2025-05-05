package gz.dmndev.restaurant.order.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;

public interface MenuServicePort {
  Optional<ProductInfo> getProduct(String productId);

  record ProductInfo(String id, String name, BigDecimal price, boolean available) {}
}
