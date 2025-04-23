package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import java.util.List;
import java.util.Optional;

public interface GetMenuItemUseCase {
  Optional<MenuItem> getMenuItemById(String id);

  List<MenuItem> getAllMenuItems();

  List<MenuItem> getMenuItemsByCategory(String categoryId);

  List<MenuItem> searchMenuItems(String query);
}
