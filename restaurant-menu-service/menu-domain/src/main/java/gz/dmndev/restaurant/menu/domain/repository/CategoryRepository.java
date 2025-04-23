package gz.dmndev.restaurant.menu.domain.repository;

import gz.dmndev.restaurant.menu.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
  Category save(Category category);

  Optional<Category> findById(String id);

  List<Category> findAll();

  List<Category> findAllActive();

  void deleteById(String id);
}
