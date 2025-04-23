package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.Category;

public interface CreateCategoryUseCase {
  Category createCategory(Category category);
}
