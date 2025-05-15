package gz.dmndev.restaurant.kitchen.domain.model;

/** Enumeration representing the various preparation states of a kitchen ticket. */
public enum PrepStatus {
  /** Ticket has been received but preparation has not started yet */
  RECEIVED,

  /** Preparation has started */
  IN_PROGRESS,

  /** Preparation is complete and the order is ready for delivery */
  READY,

  /** The order has been delivered to the customer */
  DELIVERED,

  /** The order has been cancelled */
  CANCELLED;

  /**
   * Check if the status can be transitioned to the new status following business rules
   *
   * @param newStatus The new status to transition to
   * @return true if the transition is valid, false otherwise
   */
  public boolean canTransitionTo(PrepStatus newStatus) {
    if (this == newStatus) {
      return true; // Same status is always allowed
    }

    return switch (this) {
      case RECEIVED -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
      case IN_PROGRESS -> newStatus == READY || newStatus == CANCELLED;
      case READY -> newStatus == DELIVERED || newStatus == CANCELLED;
      case DELIVERED, CANCELLED -> false; // Terminal states
    };
  }
}
