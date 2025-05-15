package gz.dmndev.restaurant.kitchen.infrastructure.adapter.in.messaging.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gz.dmndev.restaurant.common.messaging.event.BaseEvent;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OrderCreatedEvent extends BaseEvent {

  String orderId;
  String customerId;
  String customerName;
  List<OrderItemDto> items;
  BigDecimal subtotal;
  BigDecimal tax;
  BigDecimal total;
  PrepStatus status;
  String notes;

  @JsonCreator
  public OrderCreatedEvent(
          @JsonProperty("eventId") String eventId,
          @JsonProperty("eventType") String eventType,
          @JsonProperty("orderId") String orderId,
          @JsonProperty("customerId") String customerId,
          @JsonProperty("customerName") String customerName,
          @JsonProperty("items") List<OrderItemDto> items,
          @JsonProperty("subtotal") BigDecimal subtotal,
          @JsonProperty("tax") BigDecimal tax,
          @JsonProperty("total") BigDecimal total,
          @JsonProperty("status") PrepStatus status,
          @JsonProperty("notes") String notes,
          @JsonProperty("createdAt") Instant createdAt
  ) {
    super(eventId, eventType);
    this.orderId = orderId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.items = items;
    this.subtotal = subtotal;
    this.tax = tax;
    this.total = total;
    this.status = status;
    this.notes = notes;
    this.createdAt = createdAt;
  }

  public record OrderItemDto(
          String productId,
          String productName,
          Integer quantity,
          BigDecimal unitPrice,
          BigDecimal totalPrice
  ) {}
}