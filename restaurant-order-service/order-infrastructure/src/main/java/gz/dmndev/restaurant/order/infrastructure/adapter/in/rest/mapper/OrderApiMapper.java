package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.mapper;

import gz.dmndev.restaurant.order.application.port.in.CreateOrderUseCase;
import gz.dmndev.restaurant.order.application.port.in.CreateOrderUseCase.OrderItemCommand;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderItemRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderItemResponse;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderApiMapper {

  OrderResponse toOrderResponse(Order order);

  OrderItemResponse toOrderItemResponse(OrderItem item);

  default OrderItemCommand toOrderItemCommand(OrderItemRequest request) {
    return new CreateOrderUseCase.OrderItemCommand(request.productId(), request.quantity());
  }

  default List<OrderItemCommand> toOrderItemCommands(List<OrderItemRequest> requests) {
    if (requests == null) {
      return List.of();
    }
    return requests.stream().map(this::toOrderItemCommand).collect(Collectors.toList());
  }
}
