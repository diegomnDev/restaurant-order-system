package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository;

import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
  List<OrderEntity> findByCustomerId(String customerId);

  List<OrderEntity> findByStatus(OrderStatus status);
}
