package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
    basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository")
@EntityScan(
    basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity")
public class TestJpaConfig {}
