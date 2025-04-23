package gz.dmndev.restaurant.menu.infrastructure.config;

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
                .title("Menu Service API")
                .description("API for managing restaurant menu items and categories")
                .version("1.0.0")
                .contact(new Contact().name("Restaurant Team").email("contact@restaurant.com")));
  }
}
