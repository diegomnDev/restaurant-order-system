package gz.dmndev.restaurant.order.infrastructure.adapter.out.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gz.dmndev.restaurant.order.infrastructure.config.FeignConfig;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", configuration = FeignConfig.class)
@Profile("!test")
public interface MenuServiceClient {

  @GetMapping("/menu-items/{productId}")
  ProductResponse getProduct(@PathVariable("productId") String productId);

  @JsonIgnoreProperties(ignoreUnknown = true)
  record ProductResponse(String id, String name, BigDecimal price, boolean available) {}
}
