package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.mapper;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {
  Category toDomain(CategoryEntity entity);

  CategoryEntity toEntity(Category domain);
}
