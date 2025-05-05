package gz.dmndev.restaurant.order.domain.model;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
  CREATED,
  PAID,
  PREPARING,
  READY_FOR_DELIVERY,
  OUT_FOR_DELIVERY,
  DELIVERED,
  CANCELLED;

  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
      Map.ofEntries(
          Map.entry(CREATED, Set.of(PAID, CANCELLED)),
          Map.entry(PAID, Set.of(PREPARING, CANCELLED)),
          Map.entry(PREPARING, Set.of(READY_FOR_DELIVERY)),
          Map.entry(READY_FOR_DELIVERY, Set.of(OUT_FOR_DELIVERY)),
          Map.entry(OUT_FOR_DELIVERY, Set.of(DELIVERED)),
          Map.entry(DELIVERED, Set.of()),
          Map.entry(CANCELLED, Set.of()));

  public boolean canTransitionTo(OrderStatus newStatus) {
    return ALLOWED_TRANSITIONS.get(this).contains(newStatus);
  }

  public void validateTransition(OrderStatus newStatus) {
    if (this == DELIVERED || this == CANCELLED) {
      throw new IllegalStateException(
          "Cannot change status of a %s order".formatted(name().toLowerCase()));
    }
    if (!canTransitionTo(newStatus)) {
      throw new IllegalStateException(
          "Invalid transition from %s to %s".formatted(this, newStatus));
    }
  }
}
