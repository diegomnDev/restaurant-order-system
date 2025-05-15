package gz.dmndev.restaurant.kitchen.application.port.in;

import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;

/** Use case for updating the status of a kitchen ticket */
public interface UpdateTicketStatusUseCase {

  /** Command for updating ticket status */
  record UpdateTicketStatusCommand(String ticketId, PrepStatus newStatus, String chefId) {}

  /**
   * Update the status of a kitchen ticket
   *
   * @param command the command with update details
   * @return true if update was successful, false otherwise
   */
  boolean updateTicketStatus(UpdateTicketStatusCommand command);

  /**
   * Mark an item in a ticket as prepared
   *
   * @param ticketId the ID of the ticket
   * @param productId the ID of the product
   * @return true if item was marked as prepared, false otherwise
   */
  boolean markItemAsPrepared(String ticketId, String productId);

  /**
   * Start preparation of a ticket
   *
   * @param ticketId the ID of the ticket
   * @param chefId the ID of the chef assigned to the ticket
   * @return true if preparation was started, false otherwise
   */
  boolean startPreparation(String ticketId, String chefId);

  /**
   * Complete preparation of a ticket
   *
   * @param ticketId the ID of the ticket
   * @return true if preparation was completed, false otherwise
   */
  boolean completePreparation(String ticketId);

  /**
   * Cancel a ticket
   *
   * @param ticketId the ID of the ticket
   * @return true if ticket was cancelled, false otherwise
   */
  boolean cancelTicket(String ticketId);
}
