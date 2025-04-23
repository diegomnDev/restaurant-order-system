package gz.dmndev.restaurant.menu.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.menu.application.port.out.MenuItemRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

  @Mock private MenuItemRepositoryPort menuItemRepository;

  @InjectMocks private MenuItemService menuItemService;

  private MenuItem menuItem;
  private Category category;

  @BeforeEach
  void setUp() {
    category =
        Category.builder()
            .id("category-1")
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();

    menuItem =
        MenuItem.builder()
            .id("item-1")
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(category)
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .allergens(Collections.singletonList("gluten"))
            .available(true)
            .imageUrl("https://example.com/spring-rolls.jpg")
            .build();
  }

  @Test
  void createMenuItem_ShouldSaveAndReturnMenuItem() {
    // Arrange
    when(menuItemRepository.save(menuItem)).thenReturn(menuItem);

    // Act
    MenuItem result = menuItemService.createMenuItem(menuItem);

    // Assert
    assertEquals(menuItem, result);
    verify(menuItemRepository).save(menuItem);
  }

  @Test
  void getMenuItemById_WhenExists_ShouldReturnMenuItem() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.of(menuItem));

    // Act
    Optional<MenuItem> result = menuItemService.getMenuItemById("item-1");

    // Assert
    assertTrue(result.isPresent());
    assertEquals(menuItem, result.get());
  }

  @Test
  void getMenuItemById_WhenNotExists_ShouldReturnEmpty() {
    // Arrange
    when(menuItemRepository.findById("non-existent")).thenReturn(Optional.empty());

    // Act
    Optional<MenuItem> result = menuItemService.getMenuItemById("non-existent");

    // Assert
    assertFalse(result.isPresent());
  }

  @Test
  void getAllMenuItems_ShouldReturnAllItems() {
    // Arrange
    MenuItem menuItem2 =
        MenuItem.builder()
            .id("item-2")
            .name("Chicken Wings")
            .price(new BigDecimal("8.99"))
            .category(category)
            .available(true)
            .build();

    List<MenuItem> menuItems = Arrays.asList(menuItem, menuItem2);
    when(menuItemRepository.findAll()).thenReturn(menuItems);

    // Act
    List<MenuItem> result = menuItemService.getAllMenuItems();

    // Assert
    assertEquals(2, result.size());
    assertEquals(menuItems, result);
  }

  @Test
  void getMenuItemsByCategory_ShouldReturnCategoryItems() {
    // Arrange
    when(menuItemRepository.findByCategoryId("category-1"))
        .thenReturn(Collections.singletonList(menuItem));

    // Act
    List<MenuItem> result = menuItemService.getMenuItemsByCategory("category-1");

    // Assert
    assertEquals(1, result.size());
    assertEquals(menuItem, result.get(0));
  }

  @Test
  void searchMenuItems_ShouldReturnMatchingItems() {
    // Arrange
    when(menuItemRepository.findByNameContaining("Spring"))
        .thenReturn(Collections.singletonList(menuItem));

    // Act
    List<MenuItem> result = menuItemService.searchMenuItems("Spring");

    // Assert
    assertEquals(1, result.size());
    assertEquals(menuItem, result.get(0));
  }

  @Test
  void updateMenuItem_WhenExists_ShouldUpdateAndReturn() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.of(menuItem));
    when(menuItemRepository.save(menuItem)).thenReturn(menuItem);

    // Act
    MenuItem result = menuItemService.updateMenuItem(menuItem);

    // Assert
    assertEquals(menuItem, result);
    verify(menuItemRepository).save(menuItem);
  }

  @Test
  void updateMenuItem_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> menuItemService.updateMenuItem(menuItem));
    verify(menuItemRepository, never()).save(any());
  }

  @Test
  void updateMenuItemAvailability_WhenExists_ShouldUpdateAvailability() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.of(menuItem));

    MenuItem updatedItem =
        MenuItem.builder()
            .id("item-1")
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(category)
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .allergens(Collections.singletonList("gluten"))
            .available(false) // Changed availability
            .imageUrl("https://example.com/spring-rolls.jpg")
            .build();

    when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedItem);

    // Act
    MenuItem result = menuItemService.updateMenuItemAvailability("item-1", false);

    // Assert
    assertFalse(result.available());
    verify(menuItemRepository).save(any(MenuItem.class));
  }

  @Test
  void updateMenuItemAvailability_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> menuItemService.updateMenuItemAvailability("item-1", false));
    verify(menuItemRepository, never()).save(any());
  }

  @Test
  void deleteMenuItem_WhenExists_ShouldDelete() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.of(menuItem));

    // Act
    menuItemService.deleteMenuItem("item-1");

    // Assert
    verify(menuItemRepository).deleteById("item-1");
  }

  @Test
  void deleteMenuItem_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(menuItemRepository.findById("item-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> menuItemService.deleteMenuItem("item-1"));
    verify(menuItemRepository, never()).deleteById(anyString());
  }
}
