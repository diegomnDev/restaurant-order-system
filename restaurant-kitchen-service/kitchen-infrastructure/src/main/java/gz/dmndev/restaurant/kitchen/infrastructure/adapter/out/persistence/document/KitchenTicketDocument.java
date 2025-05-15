package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.persistence.document;

import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** MongoDB document representing a KitchenTicket */
@Document(collection = "kitchen_tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenTicketDocument {

  /** Unique identifier for the ticket */
  @Id private String id;

  /** Reference to the original order ID */
  @Indexed(unique = true)
  private String orderId;

  /** Customer ID for reference */
  private String customerId;

  /** Customer name for display */
  private String customerName;

  /** Items to prepare in this ticket */
  @Builder.Default private List<TicketItemDocument> items = new ArrayList<>();

  /** Current preparation status */
  @Indexed private PrepStatus status;

  /** Priority level (higher number means higher priority) */
  private int priority;

  /** Special instructions for the entire ticket */
  private String notes;

  /** Creation timestamp */
  private Instant createdAt;

  /** Last updated timestamp */
  private Instant updatedAt;

  /** When the preparation was started */
  private Instant preparationStartedAt;

  /** When the preparation was completed */
  private Instant preparationCompletedAt;

  /** Chef assigned to this ticket */
  @Indexed private String assignedTo;

  /** MongoDB document representing a TicketItem */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TicketItemDocument {

    /** ID of the product in the menu service */
    private String productId;

    /** Name of the product for display in the kitchen */
    private String productName;

    /** Quantity of this item ordered */
    private Integer quantity;

    /** Special instructions for preparing this item */
    private String specialInstructions;

    /** Whether this item has been prepared */
    private boolean prepared;
  }
}
