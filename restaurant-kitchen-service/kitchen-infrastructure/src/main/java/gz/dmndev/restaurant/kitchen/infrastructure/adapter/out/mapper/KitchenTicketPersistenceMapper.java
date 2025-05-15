package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.mapper;

import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.TicketItem;
import gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.persistence.document.KitchenTicketDocument;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Mapper for converting between domain KitchenTicket and KitchenTicketDocument */
@Component
public class KitchenTicketPersistenceMapper {

  /**
   * Convert a domain KitchenTicket to a KitchenTicketDocument
   *
   * @param ticket the domain ticket
   * @return the document representation
   */
  public KitchenTicketDocument toDocument(KitchenTicket ticket) {
    if (ticket == null) {
      return null;
    }

    return KitchenTicketDocument.builder()
        .id(ticket.getId())
        .orderId(ticket.getOrderId())
        .customerId(ticket.getCustomerId())
        .customerName(ticket.getCustomerName())
        .items(ticket.getItems().stream().map(this::toItemDocument).collect(Collectors.toList()))
        .status(ticket.getStatus())
        .priority(ticket.getPriority())
        .notes(ticket.getNotes())
        .createdAt(ticket.getCreatedAt())
        .updatedAt(ticket.getUpdatedAt())
        .preparationStartedAt(ticket.getPreparationStartedAt())
        .preparationCompletedAt(ticket.getPreparationCompletedAt())
        .assignedTo(ticket.getAssignedTo())
        .build();
  }

  /**
   * Convert a KitchenTicketDocument to a domain KitchenTicket
   *
   * @param document the document
   * @return the domain ticket
   */
  public KitchenTicket toDomain(KitchenTicketDocument document) {
    if (document == null) {
      return null;
    }

    return KitchenTicket.builder()
        .id(document.getId())
        .orderId(document.getOrderId())
        .customerId(document.getCustomerId())
        .customerName(document.getCustomerName())
        .items(document.getItems().stream().map(this::toItemDomain).collect(Collectors.toList()))
        .status(document.getStatus())
        .priority(document.getPriority())
        .notes(document.getNotes())
        .createdAt(document.getCreatedAt())
        .updatedAt(document.getUpdatedAt())
        .preparationStartedAt(document.getPreparationStartedAt())
        .preparationCompletedAt(document.getPreparationCompletedAt())
        .assignedTo(document.getAssignedTo())
        .build();
  }

  /**
   * Convert a domain TicketItem to a TicketItemDocument
   *
   * @param item the domain item
   * @return the document item
   */
  private KitchenTicketDocument.TicketItemDocument toItemDocument(TicketItem item) {
    return KitchenTicketDocument.TicketItemDocument.builder()
        .productId(item.getProductId())
        .productName(item.getProductName())
        .quantity(item.getQuantity())
        .specialInstructions(item.getSpecialInstructions())
        .prepared(item.isPrepared())
        .build();
  }

  /**
   * Convert a TicketItemDocument to a domain TicketItem
   *
   * @param itemDocument the document item
   * @return the domain item
   */
  private TicketItem toItemDomain(KitchenTicketDocument.TicketItemDocument itemDocument) {
    return TicketItem.builder()
        .productId(itemDocument.getProductId())
        .productName(itemDocument.getProductName())
        .quantity(itemDocument.getQuantity())
        .specialInstructions(itemDocument.getSpecialInstructions())
        .prepared(itemDocument.isPrepared())
        .build();
  }
}
