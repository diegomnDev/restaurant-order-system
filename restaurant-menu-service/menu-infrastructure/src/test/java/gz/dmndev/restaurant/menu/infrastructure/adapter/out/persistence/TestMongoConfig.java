package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(
    basePackages = "gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository")
public class TestMongoConfig {}
