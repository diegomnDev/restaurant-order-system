package gz.dmndev.restaurant.commonsecurity;

import gz.dmndev.restaurant.commonsecurity.config.ResourceServerConfig;
import gz.dmndev.restaurant.commonsecurity.config.SecurityProperties;
import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ResourceServerConfig.class, SecurityProperties.class})
public @interface EnableResourceServerSecurity {}
