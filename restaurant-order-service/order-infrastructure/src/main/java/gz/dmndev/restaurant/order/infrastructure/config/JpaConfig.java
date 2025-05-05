package gz.dmndev.restaurant.order.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {}
