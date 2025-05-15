package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.persistence;

import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.persistence.document.KitchenTicketDocument;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository interface for KitchenTicketDocument
 */
@Repository
public interface MongoKitchenTicketRepository extends MongoRepository<KitchenTicketDocument, String> {

    /**
     * Find a kitchen ticket document by order ID
     *
     * @param orderId the order ID
     * @return an Optional containing the kitchen ticket document if found, empty otherwise
     */
    Optional<KitchenTicketDocument> findByOrderId(String orderId);

    /**
     * Find all kitchen ticket documents with the specified status
     *
     * @param status the preparation status
     * @return a list of kitchen ticket documents with the specified status
     */
    List<KitchenTicketDocument> findByStatus(PrepStatus status);

    /**
     * Find all kitchen ticket documents assigned to a specific chef
     *
     * @param chefId the ID of the chef
     * @return a list of kitchen ticket documents assigned to the chef
     */
    List<KitchenTicketDocument> findByAssignedTo(String chefId);

    /**
     * Count tickets by status
     *
     * @param status the preparation status
     * @return the count of tickets with the specified status
     */
    long countByStatus(PrepStatus status);
}