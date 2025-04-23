package gz.dmndev.restaurant.menu.domain.repository;

import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
  MenuItem save(MenuItem menuItem);

  Optional<MenuItem> findById(String id);

  List<MenuItem> findAll();

  List<MenuItem> findByCategoryId(String categoryId);

  List<MenuItem> findByNameContaining(String name);

  void deleteById(String id);
}
