package gz.dmndev.restaurant.menu.application.port.in;

import gz.dmndev.restaurant.menu.domain.model.MenuItem;

public interface CreateMenuItemUseCase {
  MenuItem createMenuItem(MenuItem menuItem);
}
