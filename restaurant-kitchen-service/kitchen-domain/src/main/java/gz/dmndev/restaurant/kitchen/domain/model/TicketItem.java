package gz.dmndev.restaurant.kitchen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Represents an individual item within a kitchen ticket */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketItem {

  /** ID of the product in the menu service */
  private String productId;

  /** Name of the product for display in the kitchen */
  private String productName;

  /** Quantity of this item ordered */
  private Integer quantity;

  /** Special instructions for preparing this item */
  private String specialInstructions;

  /** Whether this item has been prepared */
  @Builder.Default private boolean prepared = false;

  /** Mark this item as prepared */
  public void markAsPrepared() {
    this.prepared = true;
  }

  /** Reset the preparation status */
  public void resetPreparation() {
    this.prepared = false;
  }
}
