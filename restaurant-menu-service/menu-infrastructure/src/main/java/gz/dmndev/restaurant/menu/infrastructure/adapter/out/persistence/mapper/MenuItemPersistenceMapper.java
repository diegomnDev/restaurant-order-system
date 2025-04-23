package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.mapper;

import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.MenuItemEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {CategoryPersistenceMapper.class})
public abstract class MenuItemPersistenceMapper {

  @Autowired private SpringDataCategoryRepository categoryRepository;

  @Autowired private CategoryPersistenceMapper categoryMapper;

  @Mapping(source = "categoryId", target = "category", qualifiedByName = "mapCategory")
  public abstract MenuItem toDomain(MenuItemEntity entity);

  @Mapping(source = "category", target = "categoryId", qualifiedByName = "mapCategoryId")
  public abstract MenuItemEntity toEntity(MenuItem domain);

  @Named("mapCategory")
  protected Category mapCategory(String categoryId) {
    if (categoryId == null) {
      return null;
    }

    return categoryRepository.findById(categoryId).map(categoryMapper::toDomain).orElse(null);
  }

  @Named("mapCategoryId")
  protected String mapCategoryId(Category category) {
    return category != null ? category.id() : null;
  }
}
