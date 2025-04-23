package gz.dmndev.restaurant.menu.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "gz.dmndev.restaurant.menuinfrastructure.adapter.out.persistence")
@EnableMongoAuditing
public class MongoConfig {}
