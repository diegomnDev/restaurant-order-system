package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository;

import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.MenuItemEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataMenuItemRepository extends MongoRepository<MenuItemEntity, String> {
  List<MenuItemEntity> findByCategoryId(String categoryId);

  List<MenuItemEntity> findByNameContainingIgnoreCase(String name);
}
