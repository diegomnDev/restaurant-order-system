package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.mapper;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

  @Mapping(target = "items", ignore = true)
  OrderEntity toEntity(Order order);

  Order toDomain(OrderEntity entity);

  OrderItemEntity toItemEntity(OrderItem item);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "productId", source = "productId")
  @Mapping(target = "productName", source = "productName")
  @Mapping(target = "quantity", source = "quantity")
  @Mapping(target = "unitPrice", source = "unitPrice")
  @Mapping(target = "totalPrice", source = "totalPrice")
  OrderItem toDomainItem(OrderItemEntity entity);

  default OrderEntity toEntityWithRelationships(Order order) {
    OrderEntity entity = toEntity(order);

    if (order.getItems() != null) {
      List<OrderItemEntity> itemEntities =
          order.getItems().stream().map(this::toItemEntity).collect(Collectors.toList());

      entity.setItems(itemEntities);
      itemEntities.forEach(item -> item.setOrder(entity));
    }

    return entity;
  }

  default Order toDomainWithRelationships(OrderEntity entity) {
    Order baseOrder = toDomain(entity);

    Order.OrderBuilder builder = baseOrder.toBuilder();

    if (entity.getItems() != null) {
      List<OrderItem> items =
          entity.getItems().stream().map(this::toDomainItem).collect(Collectors.toList());

      builder.items(items);
    }

    return builder.build();
  }
}
