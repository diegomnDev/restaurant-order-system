package gz.dmndev.restaurant.kitchen.application.port.in;

import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Use case for retrieving kitchen tickets */
public interface GetKitchenTicketUseCase {

  /** DTO for kitchen ticket */
  record KitchenTicketDto(
      String id,
      String orderId,
      String customerId,
      String customerName,
      List<KitchenTicketItemDto> items,
      PrepStatus status,
      int priority,
      String notes,
      Instant createdAt,
      Instant updatedAt,
      Instant preparationStartedAt,
      Instant preparationCompletedAt,
      String assignedTo,
      int preparationProgress) {}

  /** DTO for kitchen ticket item */
  record KitchenTicketItemDto(
      String productId,
      String productName,
      Integer quantity,
      String specialInstructions,
      boolean prepared) {}

  /**
   * Get a kitchen ticket by ID
   *
   * @param ticketId the ID of the ticket
   * @return an Optional containing the ticket if found, empty otherwise
   */
  Optional<KitchenTicketDto> getTicketById(String ticketId);

  /**
   * Get a kitchen ticket by the original order ID
   *
   * @param orderId the ID of the order
   * @return an Optional containing the ticket if found, empty otherwise
   */
  Optional<KitchenTicketDto> getTicketByOrderId(String orderId);

  /**
   * Get all tickets with the specified status
   *
   * @param status the preparation status
   * @return a list of tickets with the specified status
   */
  List<KitchenTicketDto> getTicketsByStatus(PrepStatus status);

  /**
   * Get all tickets assigned to a specific chef
   *
   * @param chefId the ID of the chef
   * @return a list of tickets assigned to the chef
   */
  List<KitchenTicketDto> getTicketsByChef(String chefId);

  /**
   * Get all tickets
   *
   * @return a list of all tickets
   */
  List<KitchenTicketDto> getAllTickets();

  /**
   * Get a summary of ticket counts by status
   *
   * @return a map with status as key and count as value
   */
  record KitchenStatusSummary(
      long receivedCount,
      long inProgressCount,
      long readyCount,
      long deliveredCount,
      long cancelledCount,
      long totalCount) {}

  /**
   * Get a summary of ticket counts by status
   *
   * @return a summary of ticket counts by status
   */
  KitchenStatusSummary getStatusSummary();
}
