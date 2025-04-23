package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.Category;

public interface UpdateCategoryUseCase {
  Category updateCategory(Category category);

  Category updateCategoryStatus(String id, boolean active);
}
