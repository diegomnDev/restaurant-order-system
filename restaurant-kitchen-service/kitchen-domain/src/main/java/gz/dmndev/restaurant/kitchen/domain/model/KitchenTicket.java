package gz.dmndev.restaurant.kitchen.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a kitchen ticket for order preparation
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KitchenTicket {

    /**
     * Unique identifier for the ticket
     */
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    /**
     * Reference to the original order ID
     */
    private String orderId;

    /**
     * Customer ID for reference
     */
    private String customerId;

    /**
     * Customer name for display
     */
    private String customerName;

    /**
     * Items to prepare in this ticket
     */
    @Builder.Default
    private List<TicketItem> items = new ArrayList<>();

    /**
     * Current preparation status
     */
    @Builder.Default
    private PrepStatus status = PrepStatus.RECEIVED;

    /**
     * Priority level (higher number means higher priority)
     */
    @Builder.Default
    private int priority = 1;

    /**
     * Special instructions for the entire ticket
     */
    private String notes;

    /**
     * Creation timestamp
     */
    @Builder.Default
    private Instant createdAt = Instant.now();

    /**
     * Last updated timestamp
     */
    @Builder.Default
    private Instant updatedAt = Instant.now();

    /**
     * When the preparation was started
     */
    private Instant preparationStartedAt;

    /**
     * When the preparation was completed
     */
    private Instant preparationCompletedAt;

    /**
     * Chef assigned to this ticket
     */
    private String assignedTo;

    /**
     * Update the status of this ticket
     *
     * @param newStatus the new status to set
     * @throws IllegalStateException if the status transition is not allowed
     */
    public void updateStatus(PrepStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }

        this.status = newStatus;
        this.updatedAt = Instant.now();

        // Set timestamps based on status
        if (newStatus == PrepStatus.IN_PROGRESS && this.preparationStartedAt == null) {
            this.preparationStartedAt = Instant.now();
        } else if (newStatus == PrepStatus.READY && this.preparationCompletedAt == null) {
            this.preparationCompletedAt = Instant.now();
        }
    }

    /**
     * Add an item to this ticket
     *
     * @param item the item to add
     * @return this ticket instance for method chaining
     */
    public KitchenTicket addItem(TicketItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        return this;
    }

    /**
     * Check if all items are prepared
     *
     * @return true if all items are prepared, false otherwise
     */
    public boolean allItemsPrepared() {
        if (items == null || items.isEmpty()) {
            return false;
        }

        return items.stream().allMatch(TicketItem::isPrepared);
    }

    /**
     * Mark an item as prepared by product ID
     *
     * @param productId the ID of the product to mark as prepared
     * @return true if an item was found and marked, false otherwise
     */
    public boolean markItemAsPrepared(String productId) {
        boolean found = false;

        for (TicketItem item : items) {
            if (item.getProductId().equals(productId) && !item.isPrepared()) {
                item.markAsPrepared();
                found = true;
            }
        }

        if (found) {
            this.updatedAt = Instant.now();

            // If all items are prepared, automatically update status to READY
            if (allItemsPrepared() && this.status == PrepStatus.IN_PROGRESS) {
                updateStatus(PrepStatus.READY);
            }
        }

        return found;
    }

    /**
     * Get preparation progress percentage
     *
     * @return percentage of items prepared (0-100)
     */
    public int getPreparationProgress() {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        long preparedCount = items.stream().filter(TicketItem::isPrepared).count();
        return (int)((preparedCount * 100.0) / items.size());
    }

    /**
     * Assign this ticket to a chef
     *
     * @param chefId ID of the chef to assign
     */
    public void assignTo(String chefId) {
        this.assignedTo = chefId;
        this.updatedAt = Instant.now();
    }
}