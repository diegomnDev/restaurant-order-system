package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto;

import gz.dmndev.restaurant.order.domain.model.OrderStatus;

public record UpdateStatusRequest(OrderStatus status) {}
