package gz.dmndev.restaurant.kitchen.application.port.out;

import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;

/** Output port for publishing kitchen events */
public interface KitchenEventPublisherPort {

  /**
   * Publish an event when a kitchen ticket is created
   *
   * @param kitchenTicket the created kitchen ticket
   */
  void publishTicketCreatedEvent(KitchenTicket kitchenTicket);

  /**
   * Publish an event when a kitchen ticket status is updated
   *
   * @param kitchenTicket the updated kitchen ticket
   * @param oldStatus the previous status
   * @param newStatus the new status
   */
  void publishTicketStatusUpdatedEvent(
      KitchenTicket kitchenTicket, PrepStatus oldStatus, PrepStatus newStatus);

  /**
   * Publish an event when a kitchen ticket is ready for delivery
   *
   * @param kitchenTicket the ready kitchen ticket
   */
  void publishOrderReadyEvent(KitchenTicket kitchenTicket);

  /**
   * Publish an event when a kitchen ticket is cancelled
   *
   * @param kitchenTicket the cancelled kitchen ticket
   */
  void publishOrderCancelledEvent(KitchenTicket kitchenTicket);
}
