package gz.dmndev.restaurant.kitchen.domain.repository;

import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import java.util.List;
import java.util.Optional;

/** Repository interface for KitchenTicket domain entity */
public interface KitchenTicketRepository {

  /**
   * Save a kitchen ticket
   *
   * @param kitchenTicket the ticket to save
   * @return the saved kitchen ticket
   */
  KitchenTicket save(KitchenTicket kitchenTicket);

  /**
   * Find a kitchen ticket by its ID
   *
   * @param id the ticket ID
   * @return an Optional containing the kitchen ticket if found, empty otherwise
   */
  Optional<KitchenTicket> findById(String id);

  /**
   * Find a kitchen ticket by the original order ID
   *
   * @param orderId the order ID
   * @return an Optional containing the kitchen ticket if found, empty otherwise
   */
  Optional<KitchenTicket> findByOrderId(String orderId);

  /**
   * Find all kitchen tickets with the specified status
   *
   * @param status the preparation status
   * @return a list of kitchen tickets with the specified status
   */
  List<KitchenTicket> findByStatus(PrepStatus status);

  /**
   * Find all kitchen tickets assigned to a specific chef
   *
   * @param chefId the ID of the chef
   * @return a list of kitchen tickets assigned to the chef
   */
  List<KitchenTicket> findByAssignedTo(String chefId);

  /**
   * Delete a kitchen ticket
   *
   * @param kitchenTicket the ticket to delete
   */
  void delete(KitchenTicket kitchenTicket);

  /**
   * Count tickets by status
   *
   * @param status the preparation status
   * @return the count of tickets with the specified status
   */
  long countByStatus(PrepStatus status);
}
