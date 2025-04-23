package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest;

import gz.dmndev.restaurant.menu.application.port.in.CreateMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateMenuItemUseCase;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper.MenuItemRestMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu-items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "API for menu item management")
public class MenuItemController {

  private final CreateMenuItemUseCase createMenuItemUseCase;
  private final GetMenuItemUseCase getMenuItemUseCase;
  private final UpdateMenuItemUseCase updateMenuItemUseCase;
  private final DeleteMenuItemUseCase deleteMenuItemUseCase;
  private final MenuItemRestMapper mapper;

  @PostMapping
  public ResponseEntity<MenuItemResponse> createMenuItem(
      @Valid @RequestBody MenuItemRequest request) {
    MenuItem menuItem = mapper.toDomain(request);
    MenuItem createdMenuItem = createMenuItemUseCase.createMenuItem(menuItem);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(createdMenuItem));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable String id) {
    return getMenuItemUseCase
        .getMenuItemById(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<MenuItemResponse>> getAllMenuItems(
      @RequestParam(value = "category", required = false) String categoryId,
      @RequestParam(value = "search", required = false) String searchTerm) {

    List<MenuItem> menuItems;

    if (categoryId != null) {
      menuItems = getMenuItemUseCase.getMenuItemsByCategory(categoryId);
    } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
      menuItems = getMenuItemUseCase.searchMenuItems(searchTerm);
    } else {
      menuItems = getMenuItemUseCase.getAllMenuItems();
    }

    List<MenuItemResponse> response =
        menuItems.stream().map(mapper::toResponse).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MenuItemResponse> updateMenuItem(
      @PathVariable String id, @Valid @RequestBody MenuItemRequest request) {

    return getMenuItemUseCase
        .getMenuItemById(id)
        .map(
            existingMenuItem -> {
              MenuItem menuItemToUpdate = mapper.updateDomainFromRequest(existingMenuItem, request);
              MenuItem updatedMenuItem = updateMenuItemUseCase.updateMenuItem(menuItemToUpdate);
              return ResponseEntity.ok(mapper.toResponse(updatedMenuItem));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}/availability")
  public ResponseEntity<MenuItemResponse> updateMenuItemAvailability(
      @PathVariable String id, @RequestParam("available") boolean available) {

    try {
      MenuItem updatedMenuItem = updateMenuItemUseCase.updateMenuItemAvailability(id, available);
      return ResponseEntity.ok(mapper.toResponse(updatedMenuItem));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
    try {
      deleteMenuItemUseCase.deleteMenuItem(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
