package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence;

import gz.dmndev.restaurant.menu.application.port.out.MenuItemRepositoryPort;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity.MenuItemEntity;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.mapper.MenuItemPersistenceMapper;
import gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.repository.SpringDataMenuItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuItemRepositoryAdapter implements MenuItemRepositoryPort {

  private final SpringDataMenuItemRepository repository;
  private final MenuItemPersistenceMapper mapper;

  @Override
  public MenuItem save(MenuItem menuItem) {
    MenuItemEntity entity = mapper.toEntity(menuItem);
    entity = repository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<MenuItem> findById(String id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<MenuItem> findAll() {
    return repository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<MenuItem> findByCategoryId(String categoryId) {
    return repository.findByCategoryId(categoryId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MenuItem> findByNameContaining(String name) {
    return repository.findByNameContainingIgnoreCase(name).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }
}
