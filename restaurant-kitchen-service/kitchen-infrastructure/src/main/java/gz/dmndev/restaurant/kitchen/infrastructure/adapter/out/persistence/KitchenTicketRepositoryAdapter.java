package gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.persistence;


import gz.dmndev.restaurant.kitchen.application.port.out.KitchenTicketRepositoryPort;
import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.infrastructure.adapter.out.mapper.KitchenTicketPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for the KitchenTicketRepositoryPort
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KitchenTicketRepositoryAdapter implements KitchenTicketRepositoryPort {

    private final MongoKitchenTicketRepository repository;
    private final KitchenTicketPersistenceMapper mapper;

    @Override
    public KitchenTicket save(KitchenTicket kitchenTicket) {
        log.debug("Saving kitchen ticket: {}", kitchenTicket.getId());

        var document = mapper.toDocument(kitchenTicket);
        var savedDocument = repository.save(document);

        return mapper.toDomain(savedDocument);
    }

    @Override
    public Optional<KitchenTicket> findById(String id) {
        log.debug("Finding kitchen ticket by ID: {}", id);

        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<KitchenTicket> findByOrderId(String orderId) {
        log.debug("Finding kitchen ticket by order ID: {}", orderId);

        return repository.findByOrderId(orderId)
                .map(mapper::toDomain);
    }

    @Override
    public List<KitchenTicket> findByStatus(PrepStatus status) {
        log.debug("Finding kitchen tickets by status: {}", status);

        return repository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitchenTicket> findByAssignedTo(String chefId) {
        log.debug("Finding kitchen tickets assigned to chef: {}", chefId);

        return repository.findByAssignedTo(chefId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<KitchenTicket> findAll() {
        log.debug("Finding all kitchen tickets");

        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(KitchenTicket kitchenTicket) {
        log.debug("Deleting kitchen ticket: {}", kitchenTicket.getId());

        repository.deleteById(kitchenTicket.getId());
    }

    @Override
    public long countByStatus(PrepStatus status) {
        log.debug("Counting kitchen tickets by status: {}", status);

        return repository.countByStatus(status);
    }
}