package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface GetCategoryUseCase {
  Optional<Category> getCategoryById(String id);

  List<Category> getAllCategories();

  List<Category> getAllActiveCategories();
}
