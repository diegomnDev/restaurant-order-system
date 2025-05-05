package gz.dmndev.restaurant.order.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class Order {
  private final String id;
  private final String customerId;
  private final String customerName;

  @Builder.Default private final List<OrderItem> items = new ArrayList<>();

  private BigDecimal subtotal;
  private BigDecimal tax;
  private BigDecimal total;
  private OrderStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String notes;

  // Métodos de negocio
  public void addItem(OrderItem item) {
    this.items.add(item);
    recalculateTotals();
    this.updatedAt = LocalDateTime.now();
  }

  public void removeItem(String itemId) {
    this.items.removeIf(item -> item.getId().equals(itemId));
    recalculateTotals();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateStatus(OrderStatus newStatus) {
    this.status = newStatus;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateNotes(String notes) {
    this.notes = notes;
    this.updatedAt = LocalDateTime.now();
  }

  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  private void recalculateTotals() {
    this.subtotal =
        items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    this.tax = calculateTax(this.subtotal);
    this.total = this.subtotal.add(this.tax);
  }

  private BigDecimal calculateTax(BigDecimal amount) {
    // Lógica de cálculo de impuestos (ejemplo: 10%)
    return amount.multiply(new BigDecimal("0.10"));
  }

  // Métodos de fábrica estáticos
  public static Order createNew(String id, String customerId, String customerName) {
    return Order.builder()
        .id(id)
        .customerId(customerId)
        .customerName(customerName)
        .subtotal(BigDecimal.ZERO)
        .tax(BigDecimal.ZERO)
        .total(BigDecimal.ZERO)
        .status(OrderStatus.CREATED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  // Métodos para validación de la entidad
  public boolean isValid() {
    return id != null
        && !id.isBlank()
        && customerId != null
        && !customerId.isBlank()
        && customerName != null
        && !customerName.isBlank()
        && status != null;
  }
}
