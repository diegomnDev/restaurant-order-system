package gz.dmndev.restaurant.order.infrastructure.adapter.out.rest;

import gz.dmndev.restaurant.order.application.port.out.MenuServicePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuServiceAdapter implements MenuServicePort {

  private final MenuServiceClient menuServiceClient;

  @Override
  public Optional<ProductInfo> getProduct(String productId) {
    try {
      var response = menuServiceClient.getProduct(productId);
      return Optional.of(
          new ProductInfo(response.id(), response.name(), response.price(), response.available()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }
}
