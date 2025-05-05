package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.mapper;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto.OrderItemDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderEventMapper {

  private static final String ORDER_CREATED_EVENT = "ORDER_CREATED";
  private static final String ORDER_UPDATED_EVENT = "ORDER_UPDATED";
  private static final String ORDER_CANCELLED_EVENT = "ORDER_CANCELLED";

  public OrderEventDto toOrderCreatedEventDto(Order order) {
    return toOrderEventDto(order, ORDER_CREATED_EVENT);
  }

  public OrderEventDto toOrderUpdatedEventDto(Order order) {
    return toOrderEventDto(order, ORDER_UPDATED_EVENT);
  }

  public OrderEventDto toOrderCancelledEventDto(Order order) {
    return toOrderEventDto(order, ORDER_CANCELLED_EVENT);
  }

  private OrderEventDto toOrderEventDto(Order order, String eventType) {
    List<OrderItemDto> itemDtos =
        order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList());

    return new OrderEventDto(
        UUID.randomUUID().toString(),
        eventType,
        order.getId(),
        order.getCustomerId(),
        order.getCustomerName(),
        itemDtos,
        order.getSubtotal(),
        order.getTax(),
        order.getTotal(),
        order.getStatus(),
        order.getNotes());
  }

  private OrderItemDto toOrderItemDto(OrderItem item) {
    return new OrderItemDto(
        item.getId(),
        item.getProductId(),
        item.getProductName(),
        item.getQuantity(),
        item.getUnitPrice(),
        item.getTotalPrice());
  }
}
