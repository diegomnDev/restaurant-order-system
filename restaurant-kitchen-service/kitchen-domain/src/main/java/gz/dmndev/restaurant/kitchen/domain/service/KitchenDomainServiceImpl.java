package gz.dmndev.restaurant.kitchen.domain.service;

import gz.dmndev.restaurant.kitchen.domain.exception.KitchenDomainException;
import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.domain.model.TicketItem;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Implementation of the KitchenDomainService interface */
public class KitchenDomainServiceImpl implements KitchenDomainService {

  @Override
  public boolean validateTicket(KitchenTicket kitchenTicket) {
    if (kitchenTicket == null) {
      return false;
    }

    if (kitchenTicket.getOrderId() == null || kitchenTicket.getOrderId().isBlank()) {
      return false;
    }

    if (kitchenTicket.getItems() == null || kitchenTicket.getItems().isEmpty()) {
      return false;
    }

    // Validate each item
    for (TicketItem item : kitchenTicket.getItems()) {
      if (item.getProductId() == null
          || item.getProductId().isBlank()
          || item.getProductName() == null
          || item.getProductName().isBlank()
          || item.getQuantity() == null
          || item.getQuantity() <= 0) {
        return false;
      }
    }

    return true;
  }

  @Override
  public KitchenTicket createTicket(
      String orderId,
      String customerId,
      String customerName,
      List<TicketItem> items,
      String notes) {
    KitchenTicket ticket =
        KitchenTicket.builder()
            .id(UUID.randomUUID().toString())
            .orderId(orderId)
            .customerId(customerId)
            .customerName(customerName)
            .items(items)
            .notes(notes)
            .status(PrepStatus.RECEIVED)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    if (!validateTicket(ticket)) {
      throw new KitchenDomainException("Invalid kitchen ticket data");
    }

    return ticket;
  }

  @Override
  public KitchenTicket startPreparation(KitchenTicket ticket, String chefId) {
    if (ticket == null) {
      throw new KitchenDomainException("Ticket cannot be null");
    }

    if (!PrepStatus.RECEIVED.equals(ticket.getStatus())) {
      throw new KitchenDomainException("Ticket must be in RECEIVED status to start preparation");
    }

    ticket.assignTo(chefId);
    ticket.updateStatus(PrepStatus.IN_PROGRESS);

    return ticket;
  }

  @Override
  public KitchenTicket completePreparation(KitchenTicket ticket) {
    if (ticket == null) {
      throw new KitchenDomainException("Ticket cannot be null");
    }

    if (!PrepStatus.IN_PROGRESS.equals(ticket.getStatus())) {
      throw new KitchenDomainException(
          "Ticket must be in IN_PROGRESS status to complete preparation");
    }

    // Ensure all items are marked as prepared
    if (!ticket.allItemsPrepared()) {
      ticket.getItems().forEach(TicketItem::markAsPrepared);
    }

    ticket.updateStatus(PrepStatus.READY);

    return ticket;
  }

  @Override
  public KitchenTicket updateTicketStatus(KitchenTicket ticket, PrepStatus newStatus) {
    if (ticket == null) {
      throw new KitchenDomainException("Ticket cannot be null");
    }

    if (newStatus == null) {
      throw new KitchenDomainException("New status cannot be null");
    }

    // Check if the status transition is valid
    if (!ticket.getStatus().canTransitionTo(newStatus)) {
      throw new KitchenDomainException(
          String.format("Cannot transition from %s to %s", ticket.getStatus(), newStatus));
    }

    // Perform specific operations based on the new status
    switch (newStatus) {
      case IN_PROGRESS -> {
        if (ticket.getAssignedTo() == null || ticket.getAssignedTo().isBlank()) {
          throw new KitchenDomainException(
              "Ticket must be assigned to a chef before starting preparation");
        }
      }
      case READY -> {
        // Ensure all items are prepared before marking as ready
        if (!ticket.allItemsPrepared()) {
          ticket.getItems().forEach(TicketItem::markAsPrepared);
        }
      }
      case CANCELLED -> {
        // Reset preparation status for all items
        ticket.getItems().forEach(TicketItem::resetPreparation);
      }
      default -> {
        // No special handling for other statuses
      }
    }

    ticket.updateStatus(newStatus);

    return ticket;
  }

  @Override
  public KitchenTicket markItemAsPrepared(KitchenTicket ticket, String productId) {
    if (ticket == null) {
      throw new KitchenDomainException("Ticket cannot be null");
    }

    if (productId == null || productId.isBlank()) {
      throw new KitchenDomainException("Product ID cannot be null or empty");
    }

    if (!PrepStatus.IN_PROGRESS.equals(ticket.getStatus())) {
      throw new KitchenDomainException(
          "Ticket must be in IN_PROGRESS status to mark items as prepared");
    }

    boolean itemFound = ticket.markItemAsPrepared(productId);

    if (!itemFound) {
      throw new KitchenDomainException(
          String.format("Product with ID %s not found in ticket or already prepared", productId));
    }

    return ticket;
  }

  @Override
  public KitchenTicket cancelTicket(KitchenTicket ticket) {
    if (ticket == null) {
      throw new KitchenDomainException("Ticket cannot be null");
    }

    // Only tickets in RECEIVED or IN_PROGRESS status can be cancelled
    if (PrepStatus.DELIVERED.equals(ticket.getStatus())) {
      throw new KitchenDomainException("Cannot cancel a delivered ticket");
    }

    if (PrepStatus.CANCELLED.equals(ticket.getStatus())) {
      // Already cancelled, no action needed
      return ticket;
    }

    // Reset preparation status for all items
    ticket.getItems().forEach(TicketItem::resetPreparation);

    ticket.updateStatus(PrepStatus.CANCELLED);

    return ticket;
  }
}
