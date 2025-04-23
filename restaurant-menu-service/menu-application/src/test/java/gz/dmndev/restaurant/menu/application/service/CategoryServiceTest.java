package gz.dmndev.restaurant.menu.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.menu.application.port.out.CategoryRepositoryPort;
import gz.dmndev.restaurant.menu.application.port.out.MenuItemRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
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
class CategoryServiceTest {

  @Mock private CategoryRepositoryPort categoryRepository;

  @Mock private MenuItemRepositoryPort menuItemRepository;

  @InjectMocks private CategoryService categoryService;

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
  }

  @Test
  void createCategory_ShouldSaveAndReturnCategory() {
    // Arrange
    when(categoryRepository.save(category)).thenReturn(category);

    // Act
    Category result = categoryService.createCategory(category);

    // Assert
    assertEquals(category, result);
    verify(categoryRepository).save(category);
  }

  @Test
  void getCategoryById_WhenExists_ShouldReturnCategory() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));

    // Act
    Optional<Category> result = categoryService.getCategoryById("category-1");

    // Assert
    assertTrue(result.isPresent());
    assertEquals(category, result.get());
  }

  @Test
  void getCategoryById_WhenNotExists_ShouldReturnEmpty() {
    // Arrange
    when(categoryRepository.findById("non-existent")).thenReturn(Optional.empty());

    // Act
    Optional<Category> result = categoryService.getCategoryById("non-existent");

    // Assert
    assertFalse(result.isPresent());
  }

  @Test
  void getAllCategories_ShouldReturnAllCategories() {
    // Arrange
    Category category2 =
        Category.builder()
            .id("category-2")
            .name("Main Courses")
            .displayOrder(2)
            .active(true)
            .build();

    List<Category> categories = Arrays.asList(category, category2);
    when(categoryRepository.findAll()).thenReturn(categories);

    // Act
    List<Category> result = categoryService.getAllCategories();

    // Assert
    assertEquals(2, result.size());
    assertEquals(categories, result);
  }

  @Test
  void getAllActiveCategories_ShouldReturnActiveCategories() {
    // Arrange
    when(categoryRepository.findAllActive()).thenReturn(Collections.singletonList(category));

    // Act
    List<Category> result = categoryService.getAllActiveCategories();

    // Assert
    assertEquals(1, result.size());
    assertEquals(category, result.get(0));
  }

  @Test
  void updateCategory_WhenExists_ShouldUpdateAndReturn() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));
    when(categoryRepository.save(category)).thenReturn(category);

    // Act
    Category result = categoryService.updateCategory(category);

    // Assert
    assertEquals(category, result);
    verify(categoryRepository).save(category);
  }

  @Test
  void updateCategory_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(category));
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void updateCategoryStatus_WhenExists_ShouldUpdateStatus() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));

    Category updatedCategory =
        Category.builder()
            .id("category-1")
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(false) // Changed status
            .build();

    when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

    // Act
    Category result = categoryService.updateCategoryStatus("category-1", false);

    // Assert
    assertFalse(result.active());
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  void updateCategoryStatus_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> categoryService.updateCategoryStatus("category-1", false));
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void deleteCategory_WhenExistsAndNoItems_ShouldDelete() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));
    when(menuItemRepository.findByCategoryId("category-1")).thenReturn(Collections.emptyList());

    // Act
    categoryService.deleteCategory("category-1");

    // Assert
    verify(categoryRepository).deleteById("category-1");
  }

  @Test
  void deleteCategory_WhenNotExists_ShouldThrowException() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> categoryService.deleteCategory("category-1"));
    verify(categoryRepository, never()).deleteById(anyString());
  }

  @Test
  void deleteCategory_WhenHasItems_ShouldThrowException() {
    // Arrange
    when(categoryRepository.findById("category-1")).thenReturn(Optional.of(category));
    when(menuItemRepository.findByCategoryId("category-1"))
        .thenReturn(Collections.singletonList(MenuItem.builder().id("item-1").build()));

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory("category-1"));
    verify(categoryRepository, never()).deleteById(anyString());
  }
}
