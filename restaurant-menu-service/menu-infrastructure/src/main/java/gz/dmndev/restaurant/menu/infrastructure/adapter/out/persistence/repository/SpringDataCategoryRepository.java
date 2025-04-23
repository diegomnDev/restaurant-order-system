package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository;

import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCategoryRepository extends MongoRepository<CategoryEntity, String> {
  List<CategoryEntity> findByActiveTrue();
}
