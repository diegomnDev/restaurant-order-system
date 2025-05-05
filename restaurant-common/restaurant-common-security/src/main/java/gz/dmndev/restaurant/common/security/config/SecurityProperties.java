package gz.dmndev.restaurant.common.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "restaurant.security")
public class SecurityProperties {

  private boolean enabled = true;
  private String[] publicPaths = new String[0];
}
