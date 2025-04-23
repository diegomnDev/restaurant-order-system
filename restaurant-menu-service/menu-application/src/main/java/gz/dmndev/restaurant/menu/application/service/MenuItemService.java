package gz.dmndev.restaurant.menu.application.service;

import gz.dmndev.restaurant.menu.application.port.in.CreateMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.out.MenuItemRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuItemService
    implements CreateMenuItemUseCase,
        GetMenuItemUseCase,
        UpdateMenuItemUseCase,
        DeleteMenuItemUseCase {

  private final MenuItemRepositoryPort menuItemRepository;

  @Override
  @Transactional
  public MenuItem createMenuItem(MenuItem menuItem) {
    return menuItemRepository.save(menuItem);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<MenuItem> getMenuItemById(String id) {
    return menuItemRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MenuItem> getAllMenuItems() {
    return menuItemRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<MenuItem> getMenuItemsByCategory(String categoryId) {
    return menuItemRepository.findByCategoryId(categoryId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MenuItem> searchMenuItems(String query) {
    return menuItemRepository.findByNameContaining(query);
  }

  @Override
  @Transactional
  public MenuItem updateMenuItem(MenuItem menuItem) {
    // Verificar que el item existe
    menuItemRepository
        .findById(menuItem.id())
        .orElseThrow(
            () -> new IllegalArgumentException("Menu item not found with id: " + menuItem.id()));

    return menuItemRepository.save(menuItem);
  }

  @Override
  @Transactional
  public MenuItem updateMenuItemAvailability(String id, boolean available) {
    MenuItem menuItem =
        menuItemRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + id));

    MenuItem updatedItem =
        new MenuItem(
            menuItem.id(),
            menuItem.name(),
            menuItem.description(),
            menuItem.price(),
            menuItem.category(),
            menuItem.tags(),
            menuItem.allergens(),
            available,
            menuItem.imageUrl());

    return menuItemRepository.save(updatedItem);
  }

  @Override
  @Transactional
  public void deleteMenuItem(String id) {
    // Verificar que el item existe
    menuItemRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + id));

    menuItemRepository.deleteById(id);
  }
}
