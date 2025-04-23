package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper;

import gz.dmndev.restaurant.menu.application.service.CategoryService;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {CategoryRestMapper.class})
public abstract class MenuItemRestMapper {

  @Autowired private CategoryService categoryService;

  @Mapping(source = "categoryId", target = "category")
  @Mapping(target = "id", ignore = true)
  public abstract MenuItem toDomain(MenuItemRequest request);

  public abstract MenuItemResponse toResponse(MenuItem domain);

  public MenuItem updateDomainFromRequest(MenuItem existingItem, MenuItemRequest request) {
    Category category = mapCategory(request.getCategoryId());

    return new MenuItem(
        existingItem.id(),
        request.getName() != null ? request.getName() : existingItem.name(),
        request.getDescription() != null ? request.getDescription() : existingItem.description(),
        request.getPrice() != null ? request.getPrice() : existingItem.price(),
        category != null ? category : existingItem.category(),
        request.getTags() != null ? request.getTags() : existingItem.tags(),
        request.getAllergens() != null ? request.getAllergens() : existingItem.allergens(),
        request.isAvailable(),
        request.getImageUrl() != null ? request.getImageUrl() : existingItem.imageUrl());
  }

  protected Category mapCategory(String categoryId) {
    if (categoryId == null) {
      return null;
    }

    return categoryService
        .getCategoryById(categoryId)
        .orElseThrow(
            () -> new IllegalArgumentException("Category not found with id: " + categoryId));
  }
}
