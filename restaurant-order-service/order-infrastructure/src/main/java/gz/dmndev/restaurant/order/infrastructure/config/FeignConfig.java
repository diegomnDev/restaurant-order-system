package gz.dmndev.restaurant.order.infrastructure.config;

import gz.dmndev.restaurant.common.security.feign.FeignClientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  public FeignClientInterceptor feignClientInterceptor() {
    return new FeignClientInterceptor();
  }
}
