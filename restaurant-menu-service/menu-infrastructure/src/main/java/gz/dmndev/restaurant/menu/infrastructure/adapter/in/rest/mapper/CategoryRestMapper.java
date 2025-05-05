package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
  @Mapping(target = "id", ignore = true)
  Category toDomain(CategoryRequest request);

  CategoryResponse toResponse(Category domain);

  default Category updateDomainFromRequest(Category category, CategoryRequest request) {
    if (request == null) return category;

    Category.Builder builder = category.toBuilder();

    if (request.getName() != null) {
      builder.name(request.getName());
    }
    if (request.getDescription() != null) {
      builder.description(request.getDescription());
    }
    if (request.getDisplayOrder() != null) {
      builder.displayOrder(request.getDisplayOrder());
    }
    builder.active(request.isActive());

    return builder.build();
  }
}
