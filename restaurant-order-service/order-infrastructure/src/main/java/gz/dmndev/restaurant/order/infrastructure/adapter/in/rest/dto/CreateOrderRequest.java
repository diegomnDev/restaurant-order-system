package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record CreateOrderRequest(
    String customerId, String customerName, List<OrderItemRequest> items, String notes) {}
