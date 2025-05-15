package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing an order prepared event to be published to Kafka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreparedEvent {

    /**
     * ID of the event
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * ID of the order
     */
    private String orderId;

    /**
     * ID of the kitchen ticket
     */
    private String kitchenTicketId;

    /**
     * ID of the customer
     */
    private String customerId;

    /**
     * Name of the customer
     */
    private String customerName;

    /**
     * ID of the chef who prepared the order
     */
    private String preparedBy;

    /**
     * Timestamp when the order was prepared
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private Instant preparedAt = Instant.now();

    /**
     * Any additional notes
     */
    private String notes;
}