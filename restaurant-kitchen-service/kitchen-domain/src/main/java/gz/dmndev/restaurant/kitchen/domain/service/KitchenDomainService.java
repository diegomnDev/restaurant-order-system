package gz.dmndev.restaurant.kitchen.domain.service;

import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.domain.model.TicketItem;
import java.util.List;

/**
 * Domain service interface for kitchen operations
 */
public interface KitchenDomainService {

    /**
     * Validate a kitchen ticket
     *
     * @param kitchenTicket the ticket to validate
     * @return true if the ticket is valid, false otherwise
     */
    boolean validateTicket(KitchenTicket kitchenTicket);

    /**
     * Create a new kitchen ticket
     *
     * @param orderId the order ID
     * @param customerId the customer ID
     * @param customerName the customer name
     * @param items the list of items to prepare
     * @param notes any special notes for the ticket
     * @return the created kitchen ticket
     */
    KitchenTicket createTicket(String orderId, String customerId, String customerName,
                               List<TicketItem> items, String notes);

    /**
     * Start preparation of a ticket
     *
     * @param ticket the ticket to start preparing
     * @param chefId the ID of the chef assigned to the ticket
     * @return the updated kitchen ticket
     */
    KitchenTicket startPreparation(KitchenTicket ticket, String chefId);

    /**
     * Complete preparation of a ticket
     *
     * @param ticket the ticket that is ready
     * @return the updated kitchen ticket
     */
    KitchenTicket completePreparation(KitchenTicket ticket);

    /**
     * Update the status of a ticket
     *
     * @param ticket the ticket to update
     * @param newStatus the new status
     * @return the updated kitchen ticket
     */
    KitchenTicket updateTicketStatus(KitchenTicket ticket, PrepStatus newStatus);

    /**
     * Mark an item in a ticket as prepared
     *
     * @param ticket the ticket containing the item
     * @param productId the product ID of the item
     * @return the updated kitchen ticket
     */
    KitchenTicket markItemAsPrepared(KitchenTicket ticket, String productId);

    /**
     * Cancel a ticket
     *
     * @param ticket the ticket to cancel
     * @return the updated kitchen ticket
     */
    KitchenTicket cancelTicket(KitchenTicket ticket);
}