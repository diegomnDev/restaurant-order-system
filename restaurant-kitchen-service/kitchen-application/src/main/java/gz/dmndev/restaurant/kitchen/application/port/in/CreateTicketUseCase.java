package gz.dmndev.restaurant.kitchen.application.port.in;

import java.util.List;

/** Use case for creating a new kitchen ticket */
public interface CreateTicketUseCase {

  /** Command for creating a new kitchen ticket */
  record CreateTicketCommand(
      String orderId,
      String customerId,
      String customerName,
      List<CreateTicketItemCommand> items,
      String notes,
      Integer priority) {}

  /** Command for creating a ticket item */
  record CreateTicketItemCommand(
      String productId, String productName, Integer quantity, String specialInstructions) {}

  /**
   * Create a new kitchen ticket
   *
   * @param command the command with ticket details
   * @return the ID of the created ticket
   */
  String createTicket(CreateTicketCommand command);
}
