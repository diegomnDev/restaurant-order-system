package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gz.dmndev.restaurant.common.messaging.event.BaseEvent;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OrderEventDto extends BaseEvent {

  String orderId;
  String customerId;
  String customerName;
  List<OrderItemDto> items;
  BigDecimal subtotal;
  BigDecimal tax;
  BigDecimal total;
  OrderStatus status;
  String notes;

  @JsonCreator
  public OrderEventDto(
      @JsonProperty("eventId") String eventId,
      @JsonProperty("eventType") String eventType,
      @JsonProperty("orderId") String orderId,
      @JsonProperty("customerId") String customerId,
      @JsonProperty("customerName") String customerName,
      @JsonProperty("items") List<OrderItemDto> items,
      @JsonProperty("subtotal") BigDecimal subtotal,
      @JsonProperty("tax") BigDecimal tax,
      @JsonProperty("total") BigDecimal total,
      @JsonProperty("status") OrderStatus status,
      @JsonProperty("notes") String notes) {
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
  }

  public record OrderItemDto(
      String id,
      String productId,
      String productName,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice) {}
}
