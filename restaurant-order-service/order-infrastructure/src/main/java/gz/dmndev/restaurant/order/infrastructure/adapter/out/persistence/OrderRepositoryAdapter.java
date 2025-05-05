package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence;

import gz.dmndev.restaurant.order.application.port.out.OrderRepositoryPort;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderRepositoryAdapter implements OrderRepositoryPort {

  private final OrderJpaRepository orderRepository;
  private final OrderPersistenceMapper mapper;

  @Override
  public Order save(Order order) {
    var entity = mapper.toEntityWithRelationships(order);
    var savedEntity = orderRepository.save(entity);
    return mapper.toDomainWithRelationships(savedEntity);
  }

  @Override
  public Optional<Order> findById(String id) {
    return orderRepository.findById(id).map(mapper::toDomainWithRelationships);
  }

  @Override
  public List<Order> findByCustomerId(String customerId) {
    return orderRepository.findByCustomerId(customerId).stream()
        .map(mapper::toDomainWithRelationships)
        .collect(Collectors.toList());
  }

  @Override
  public List<Order> findByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status).stream()
        .map(mapper::toDomainWithRelationships)
        .collect(Collectors.toList());
  }

  @Override
  public List<Order> findAll() {
    return orderRepository.findAll().stream()
        .map(mapper::toDomainWithRelationships)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(String id) {
    orderRepository.deleteById(id);
  }
}
