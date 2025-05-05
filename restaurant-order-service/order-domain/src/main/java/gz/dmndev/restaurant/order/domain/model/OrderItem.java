package gz.dmndev.restaurant.order.domain.model;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class OrderItem {
  private final String id;
  private final String productId;
  private final String productName;
  private int quantity;
  private final BigDecimal unitPrice;
  private BigDecimal totalPrice;

  public void updateQuantity(int newQuantity) {
    if (newQuantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    this.quantity = newQuantity;
    recalculateTotalPrice();
  }

  private void recalculateTotalPrice() {
    this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
  }

  public static OrderItem createNew(
      String id, String productId, String productName, int quantity, BigDecimal unitPrice) {

    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Unit price must be greater than zero");
    }

    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

    return OrderItem.builder()
        .id(id)
        .productId(productId)
        .productName(productName)
        .quantity(quantity)
        .unitPrice(unitPrice)
        .totalPrice(totalPrice)
        .build();
  }

  // Método para validar la entidad
  public boolean isValid() {
    return id != null
        && !id.isBlank()
        && productId != null
        && !productId.isBlank()
        && productName != null
        && !productName.isBlank()
        && quantity > 0
        && unitPrice != null
        && unitPrice.compareTo(BigDecimal.ZERO) > 0
        && totalPrice != null
        && totalPrice.compareTo(BigDecimal.ZERO) > 0;
  }
}
