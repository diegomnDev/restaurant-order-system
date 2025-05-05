package gz.dmndev.restaurant.menu.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.MenuItemEntity;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class MongoDataInitializer implements ApplicationRunner {

  private final MongoTemplate mongoTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public MongoDataInitializer(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
    this.mongoTemplate = mongoTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (mongoTemplate.findAll(CategoryEntity.class).isEmpty()) {
      loadCategories();
      loadProducts();
    }
  }

  private void loadCategories() throws IOException {
    Resource categoriesResource = new ClassPathResource("data/categories.json");
    CategoryEntity[] categories =
        objectMapper.readValue(categoriesResource.getInputStream(), CategoryEntity[].class);
    Arrays.stream(categories).forEach(mongoTemplate::save);
    log.info("Loaded {} categories", categories.length);
  }

  private void loadProducts() throws IOException {
    Resource productsResource = new ClassPathResource("data/menuItems.json");
    MenuItemEntity[] menuItems =
        objectMapper.readValue(productsResource.getInputStream(), MenuItemEntity[].class);
    Arrays.stream(menuItems).forEach(mongoTemplate::save);
    log.info("Loaded {} menuItems", menuItems.length);
  }
}
