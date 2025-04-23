package gz.dmndev.restaurant.menu.application.service;

import gz.dmndev.restaurant.menu.application.port.in.CreateCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.out.CategoryRepositoryPort;
import gz.dmndev.restaurant.menu.application.port.out.MenuItemRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.Category;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService
    implements CreateCategoryUseCase,
        GetCategoryUseCase,
        UpdateCategoryUseCase,
        DeleteCategoryUseCase {

  private final CategoryRepositoryPort categoryRepository;
  private final MenuItemRepositoryPort menuItemRepository;

  @Override
  @Transactional
  public Category createCategory(Category category) {
    return categoryRepository.save(category);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Category> getCategoryById(String id) {
    return categoryRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Category> getAllActiveCategories() {
    return categoryRepository.findAllActive();
  }

  @Override
  @Transactional
  public Category updateCategory(Category category) {
    // Verificar que la categoría existe
    categoryRepository
        .findById(category.id())
        .orElseThrow(
            () -> new IllegalArgumentException("Category not found with id: " + category.id()));

    return categoryRepository.save(category);
  }

  @Override
  @Transactional
  public Category updateCategoryStatus(String id, boolean active) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

    Category updatedCategory =
        new Category(
            category.id(),
            category.name(),
            category.description(),
            category.displayOrder(),
            active);

    return categoryRepository.save(updatedCategory);
  }

  @Override
  @Transactional
  public void deleteCategory(String id) {
    // Verificar que la categoría existe
    categoryRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

    // Verificar si hay productos en esta categoría
    if (!menuItemRepository.findByCategoryId(id).isEmpty()) {
      throw new IllegalStateException("Cannot delete a category that has menu items");
    }

    categoryRepository.deleteById(id);
  }
}
