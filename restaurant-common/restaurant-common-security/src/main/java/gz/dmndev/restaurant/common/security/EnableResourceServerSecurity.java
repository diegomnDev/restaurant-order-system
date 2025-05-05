package gz.dmndev.restaurant.common.security;

import gz.dmndev.restaurant.common.security.config.ResourceServerConfig;
import gz.dmndev.restaurant.common.security.config.SecurityProperties;
import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ResourceServerConfig.class, SecurityProperties.class})
public @interface EnableResourceServerSecurity {}
