package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    String id,
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice) {}
