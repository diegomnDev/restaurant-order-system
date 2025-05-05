package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto;

import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id,
    String customerId,
    String customerName,
    List<OrderItemResponse> items,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal total,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String notes) {}
