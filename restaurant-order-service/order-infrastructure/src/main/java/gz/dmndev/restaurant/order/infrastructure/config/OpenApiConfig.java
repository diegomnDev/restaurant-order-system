package gz.dmndev.restaurant.order.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI menuServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Order Service API")
                .description("API for managing restaurant orders")
                .version("1.0.0")
                .contact(new Contact().name("Restaurant Team").email("contact@restaurant.com")));
  }
}
