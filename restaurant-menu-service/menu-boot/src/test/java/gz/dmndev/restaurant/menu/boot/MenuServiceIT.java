package gz.dmndev.restaurant.menu.boot;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MenuServiceIT {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    registry.add("spring.data.mongodb.database", () -> "testdb");
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  private String baseUrl;
  private CategoryResponse createdCategory;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port;

    // Create a test category first
    CategoryRequest categoryRequest =
        CategoryRequest.builder()
            .name("Test Category")
            .description("Category for integration tests")
            .displayOrder(1)
            .active(true)
            .build();

    ResponseEntity<CategoryResponse> response =
        restTemplate.postForEntity(
            baseUrl + "/categories", categoryRequest, CategoryResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    createdCategory = response.getBody();
    assertNotNull(createdCategory);
    assertNotNull(createdCategory.getId());
  }

  @Test
  void fullCrudFlow_ShouldWorkEndToEnd() {
    // Create a menu item
    MenuItemRequest createRequest =
        MenuItemRequest.builder()
            .name("Integration Test Item")
            .description("Item for integration test")
            .price(new BigDecimal("10.99"))
            .categoryId(createdCategory.getId())
            .tags(Arrays.asList("test", "integration"))
            .available(true)
            .build();

    ResponseEntity<MenuItemResponse> createResponse =
        restTemplate.postForEntity(baseUrl + "/menu-items", createRequest, MenuItemResponse.class);

    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    MenuItemResponse createdItem = createResponse.getBody();
    assertNotNull(createdItem);
    assertNotNull(createdItem.getId());
    assertEquals("Integration Test Item", createdItem.getName());

    // Get the menu item by ID
    ResponseEntity<MenuItemResponse> getResponse =
        restTemplate.getForEntity(
            baseUrl + "/menu-items/" + createdItem.getId(), MenuItemResponse.class);

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertEquals(createdItem.getId(), getResponse.getBody().getId());

    // Update the menu item
    MenuItemRequest updateRequest =
        MenuItemRequest.builder()
            .name("Updated Integration Test Item")
            .description("Updated description")
            .price(new BigDecimal("12.99"))
            .categoryId(createdCategory.getId())
            .tags(Arrays.asList("test", "integration", "updated"))
            .available(true)
            .build();

    ResponseEntity<MenuItemResponse> updateResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items/" + createdItem.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest),
            MenuItemResponse.class);

    assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    MenuItemResponse updatedItem = updateResponse.getBody();
    assertNotNull(updatedItem);
    assertEquals("Updated Integration Test Item", updatedItem.getName());
    assertEquals(new BigDecimal("12.99"), updatedItem.getPrice());

    // Get all menu items
    ResponseEntity<List<MenuItemResponse>> getAllResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MenuItemResponse>>() {});

    assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
    List<MenuItemResponse> allItems = getAllResponse.getBody();
    assertNotNull(allItems);
    assertTrue(allItems.size() > 0);
    assertTrue(allItems.stream().anyMatch(item -> item.getId().equals(createdItem.getId())));

    // Get menu items by category
    ResponseEntity<List<MenuItemResponse>> getByCategoryResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items?category=" + createdCategory.getId(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MenuItemResponse>>() {});

    assertEquals(HttpStatus.OK, getByCategoryResponse.getStatusCode());
    List<MenuItemResponse> categoryItems = getByCategoryResponse.getBody();
    assertNotNull(categoryItems);
    assertTrue(categoryItems.size() > 0);
    assertTrue(categoryItems.stream().anyMatch(item -> item.getId().equals(createdItem.getId())));

    // Update availability
    ResponseEntity<MenuItemResponse> availabilityResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items/" + createdItem.getId() + "/availability?available=false",
            HttpMethod.PATCH,
            null,
            MenuItemResponse.class);

    assertEquals(HttpStatus.OK, availabilityResponse.getStatusCode());
    assertFalse(availabilityResponse.getBody().isAvailable());

    // Delete the menu item
    ResponseEntity<Void> deleteResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items/" + createdItem.getId(), HttpMethod.DELETE, null, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    // Verify it's deleted
    ResponseEntity<MenuItemResponse> getDeletedResponse =
        restTemplate.getForEntity(
            baseUrl + "/menu-items/" + createdItem.getId(), MenuItemResponse.class);

    assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
  }

  @Test
  void searchMenuItems_ShouldReturnMatchingItems() {
    // Create menu items with specific names for searching
    MenuItemRequest item1Request =
        MenuItemRequest.builder()
            .name("Chicken Soup")
            .price(new BigDecimal("7.99"))
            .categoryId(createdCategory.getId())
            .available(true)
            .build();

    MenuItemRequest item2Request =
        MenuItemRequest.builder()
            .name("Beef Steak")
            .price(new BigDecimal("19.99"))
            .categoryId(createdCategory.getId())
            .available(true)
            .build();

    restTemplate.postForEntity(baseUrl + "/menu-items", item1Request, MenuItemResponse.class);
    restTemplate.postForEntity(baseUrl + "/menu-items", item2Request, MenuItemResponse.class);

    // Search for items containing "Chicken"
    ResponseEntity<List<MenuItemResponse>> searchResponse =
        restTemplate.exchange(
            baseUrl + "/menu-items?search=Chicken",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MenuItemResponse>>() {});

    assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
    List<MenuItemResponse> searchResults = searchResponse.getBody();
    assertNotNull(searchResults);
    assertTrue(searchResults.size() > 0);
    assertTrue(searchResults.stream().allMatch(item -> item.getName().contains("Chicken")));
    assertFalse(searchResults.stream().anyMatch(item -> item.getName().contains("Beef")));
  }

  @Test
  void categoryCrudFlow_ShouldWorkEndToEnd() {
    // Create a new category
    CategoryRequest createRequest =
        CategoryRequest.builder()
            .name("Desserts")
            .description("Sweet treats")
            .displayOrder(2)
            .active(true)
            .build();

    ResponseEntity<CategoryResponse> createResponse =
        restTemplate.postForEntity(baseUrl + "/categories", createRequest, CategoryResponse.class);

    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    CategoryResponse createdCategory = createResponse.getBody();
    assertNotNull(createdCategory);
    assertNotNull(createdCategory.getId());
    assertEquals("Desserts", createdCategory.getName());

    // Update the category
    CategoryRequest updateRequest =
        CategoryRequest.builder()
            .name("Updated Desserts")
            .description("Updated description")
            .displayOrder(3)
            .active(true)
            .build();

    ResponseEntity<CategoryResponse> updateResponse =
        restTemplate.exchange(
            baseUrl + "/categories/" + createdCategory.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest),
            CategoryResponse.class);

    assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    CategoryResponse updatedCategory = updateResponse.getBody();
    assertNotNull(updatedCategory);
    assertEquals("Updated Desserts", updatedCategory.getName());
    assertEquals(3, updatedCategory.getDisplayOrder());

    // Get all categories
    ResponseEntity<List<CategoryResponse>> getAllResponse =
        restTemplate.exchange(
            baseUrl + "/categories",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CategoryResponse>>() {});

    assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
    List<CategoryResponse> allCategories = getAllResponse.getBody();
    assertNotNull(allCategories);
    assertTrue(allCategories.size() >= 2); // At least the one created in setup and this one

    // Get active categories only
    ResponseEntity<List<CategoryResponse>> getActiveResponse =
        restTemplate.exchange(
            baseUrl + "/categories?active=true",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CategoryResponse>>() {});

    assertEquals(HttpStatus.OK, getActiveResponse.getStatusCode());
    List<CategoryResponse> activeCategories = getActiveResponse.getBody();
    assertNotNull(activeCategories);
    assertTrue(activeCategories.stream().allMatch(CategoryResponse::isActive));

    // Update category status to inactive
    ResponseEntity<CategoryResponse> statusResponse =
        restTemplate.exchange(
            baseUrl + "/categories/" + createdCategory.getId() + "/status?active=false",
            HttpMethod.PATCH,
            null,
            CategoryResponse.class);

    assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
    assertFalse(statusResponse.getBody().isActive());

    // Delete the category
    ResponseEntity<Void> deleteResponse =
        restTemplate.exchange(
            baseUrl + "/categories/" + createdCategory.getId(),
            HttpMethod.DELETE,
            null,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    // Verify it's deleted
    ResponseEntity<CategoryResponse> getDeletedResponse =
        restTemplate.getForEntity(
            baseUrl + "/categories/" + createdCategory.getId(), CategoryResponse.class);

    assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
  }
}
