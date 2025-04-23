package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.MenuItem;

public interface UpdateMenuItemUseCase {
  MenuItem updateMenuItem(MenuItem menuItem);

  MenuItem updateMenuItemAvailability(String id, boolean available);
}
