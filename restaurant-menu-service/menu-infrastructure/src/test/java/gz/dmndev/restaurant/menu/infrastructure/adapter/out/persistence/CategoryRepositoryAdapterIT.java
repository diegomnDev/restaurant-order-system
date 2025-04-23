package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest(classes = TestMongoConfig.class)
@Testcontainers
class CategoryRepositoryAdapterIT {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    registry.add("spring.data.mongodb.database", () -> "testdb");
  }

  @Autowired private CategoryRepositoryAdapter categoryRepositoryAdapter;

  @Autowired private SpringDataCategoryRepository springDataCategoryRepository;

  @BeforeEach
  void setUp() {
    springDataCategoryRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    springDataCategoryRepository.deleteAll();
  }

  @Test
  void save_ShouldCreateNewCategory() {
    // Arrange
    Category category =
        Category.builder()
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();

    // Act
    Category savedCategory = categoryRepositoryAdapter.save(category);

    // Assert
    assertNotNull(savedCategory.id());
    assertEquals("Appetizers", savedCategory.name());
    assertEquals("Starters", savedCategory.description());
    assertEquals(1, savedCategory.displayOrder());
    assertTrue(savedCategory.active());

    // Verify in DB
    Optional<CategoryEntity> dbCategory = springDataCategoryRepository.findById(savedCategory.id());
    assertTrue(dbCategory.isPresent());
    assertEquals("Appetizers", dbCategory.get().getName());
  }

  @Test
  void findById_WhenExists_ShouldReturnCategory() {
    // Arrange
    CategoryEntity entity =
        CategoryEntity.builder()
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();

    entity = springDataCategoryRepository.save(entity);

    // Act
    Optional<Category> foundCategory = categoryRepositoryAdapter.findById(entity.getId());

    // Assert
    assertTrue(foundCategory.isPresent());
    assertEquals(entity.getId(), foundCategory.get().id());
    assertEquals(entity.getName(), foundCategory.get().name());
  }

  @Test
  void findById_WhenNotExists_ShouldReturnEmpty() {
    // Act
    Optional<Category> foundCategory = categoryRepositoryAdapter.findById("non-existent-id");

    // Assert
    assertFalse(foundCategory.isPresent());
  }

  @Test
  void findAll_ShouldReturnAllCategories() {
    // Arrange
    CategoryEntity entity1 =
        CategoryEntity.builder().name("Appetizers").displayOrder(1).active(true).build();

    CategoryEntity entity2 =
        CategoryEntity.builder().name("Main Courses").displayOrder(2).active(true).build();

    springDataCategoryRepository.saveAll(List.of(entity1, entity2));

    // Act
    List<Category> categories = categoryRepositoryAdapter.findAll();

    // Assert
    assertEquals(2, categories.size());
  }

  @Test
  void findAllActive_ShouldReturnOnlyActiveCategories() {
    // Arrange
    CategoryEntity entity1 =
        CategoryEntity.builder().name("Appetizers").displayOrder(1).active(true).build();

    CategoryEntity entity2 =
        CategoryEntity.builder().name("Inactive Category").displayOrder(2).active(false).build();

    springDataCategoryRepository.saveAll(List.of(entity1, entity2));

    // Act
    List<Category> activeCategories = categoryRepositoryAdapter.findAllActive();

    // Assert
    assertEquals(1, activeCategories.size());
    assertEquals("Appetizers", activeCategories.get(0).name());
  }

  @Test
  void deleteById_ShouldRemoveCategory() {
    // Arrange
    CategoryEntity entity =
        CategoryEntity.builder().name("Category to delete").displayOrder(1).active(true).build();

    entity = springDataCategoryRepository.save(entity);
    String idToDelete = entity.getId();

    // Act
    categoryRepositoryAdapter.deleteById(idToDelete);

    // Assert
    assertFalse(springDataCategoryRepository.existsById(idToDelete));
  }
}
