package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
  @Mapping(target = "id", ignore = true)
  Category toDomain(CategoryRequest request);

  CategoryResponse toResponse(Category domain);

  @Mapping(target = "id", ignore = true)
  void updateDomainFromRequest(@MappingTarget Category category, CategoryRequest request);
}
