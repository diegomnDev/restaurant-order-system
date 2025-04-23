package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence;

import gz.dmndev.restaurant.menu.application.port.out.CategoryRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.mapper.CategoryPersistenceMapper;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataCategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

  private final SpringDataCategoryRepository repository;
  private final CategoryPersistenceMapper mapper;

  @Override
  public Category save(Category category) {
    CategoryEntity entity = mapper.toEntity(category);
    entity = repository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Category> findById(String id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Category> findAll() {
    return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Category> findAllActive() {
    return repository.findByActiveTrue().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
