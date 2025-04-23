package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.MenuItemEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.mapper.CategoryPersistenceMapper;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataCategoryRepository;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataMenuItemRepository;
import java.math.BigDecimal;
import java.util.Arrays;
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
class MenuItemRepositoryAdapterIT {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    registry.add("spring.data.mongodb.database", () -> "testdb");
  }

  @Autowired private MenuItemRepositoryAdapter menuItemRepositoryAdapter;

  @Autowired private SpringDataMenuItemRepository springDataMenuItemRepository;

  @Autowired private SpringDataCategoryRepository springDataCategoryRepository;

  @Autowired private CategoryPersistenceMapper categoryMapper;

  private CategoryEntity categoryEntity;
  private Category category;

  @BeforeEach
  void setUp() {
    springDataMenuItemRepository.deleteAll();
    springDataCategoryRepository.deleteAll();

    // Create a category first
    categoryEntity =
        CategoryEntity.builder()
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();

    categoryEntity = springDataCategoryRepository.save(categoryEntity);
    category = categoryMapper.toDomain(categoryEntity);
  }

  @AfterEach
  void tearDown() {
    springDataMenuItemRepository.deleteAll();
    springDataCategoryRepository.deleteAll();
  }

  @Test
  void save_ShouldCreateNewMenuItem() {
    // Arrange
    MenuItem menuItem =
        MenuItem.builder()
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(category)
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .available(true)
            .build();

    // Act
    MenuItem savedMenuItem = menuItemRepositoryAdapter.save(menuItem);

    // Assert
    assertNotNull(savedMenuItem.id());
    assertEquals("Spring Rolls", savedMenuItem.name());
    assertEquals(new BigDecimal("5.99"), savedMenuItem.price());
    assertEquals(category.id(), savedMenuItem.category().id());

    // Verify in DB
    Optional<MenuItemEntity> dbMenuItem = springDataMenuItemRepository.findById(savedMenuItem.id());
    assertTrue(dbMenuItem.isPresent());
    assertEquals("Spring Rolls", dbMenuItem.get().getName());
    assertEquals(categoryEntity.getId(), dbMenuItem.get().getCategoryId());
  }

  @Test
  void findById_WhenExists_ShouldReturnMenuItem() {
    // Arrange
    MenuItemEntity entity =
        MenuItemEntity.builder()
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    entity = springDataMenuItemRepository.save(entity);

    // Act
    Optional<MenuItem> foundMenuItem = menuItemRepositoryAdapter.findById(entity.getId());

    // Assert
    assertTrue(foundMenuItem.isPresent());
    assertEquals(entity.getId(), foundMenuItem.get().id());
    assertEquals(entity.getName(), foundMenuItem.get().name());
    assertEquals(category.id(), foundMenuItem.get().category().id());
  }

  @Test
  void findByCategoryId_ShouldReturnCategoryItems() {
    // Arrange
    MenuItemEntity entity1 =
        MenuItemEntity.builder()
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    MenuItemEntity entity2 =
        MenuItemEntity.builder()
            .name("Chicken Wings")
            .price(new BigDecimal("7.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    springDataMenuItemRepository.saveAll(List.of(entity1, entity2));

    // Create another category with items
    CategoryEntity otherCategory =
        CategoryEntity.builder().name("Desserts").displayOrder(2).active(true).build();

    otherCategory = springDataCategoryRepository.save(otherCategory);

    MenuItemEntity dessertItem =
        MenuItemEntity.builder()
            .name("Ice Cream")
            .price(new BigDecimal("3.99"))
            .categoryId(otherCategory.getId())
            .available(true)
            .build();

    springDataMenuItemRepository.save(dessertItem);

    // Act
    List<MenuItem> categoryItems =
        menuItemRepositoryAdapter.findByCategoryId(categoryEntity.getId());

    // Assert
    assertEquals(2, categoryItems.size());
    assertTrue(
        categoryItems.stream()
            .allMatch(item -> item.category().id().equals(categoryEntity.getId())));
  }

  @Test
  void findByNameContaining_ShouldReturnMatchingItems() {
    // Arrange
    MenuItemEntity entity1 =
        MenuItemEntity.builder()
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    MenuItemEntity entity2 =
        MenuItemEntity.builder()
            .name("Chicken Wings")
            .price(new BigDecimal("7.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    springDataMenuItemRepository.saveAll(List.of(entity1, entity2));

    // Act
    List<MenuItem> matchingItems = menuItemRepositoryAdapter.findByNameContaining("Spring");

    // Assert
    assertEquals(1, matchingItems.size());
    assertEquals("Spring Rolls", matchingItems.get(0).name());
  }

  @Test
  void deleteById_ShouldRemoveMenuItem() {
    // Arrange
    MenuItemEntity entity =
        MenuItemEntity.builder()
            .name("Item to delete")
            .price(new BigDecimal("5.99"))
            .categoryId(categoryEntity.getId())
            .available(true)
            .build();

    entity = springDataMenuItemRepository.save(entity);
    String idToDelete = entity.getId();

    // Act
    menuItemRepositoryAdapter.deleteById(idToDelete);

    // Assert
    assertFalse(springDataMenuItemRepository.existsById(idToDelete));
  }
}
