package gz.dmndev.restaurant.kitchen.application.service;

import gz.dmndev.restaurant.kitchen.application.exception.KitchenApplicationException;
import gz.dmndev.restaurant.kitchen.application.port.in.CreateTicketUseCase;
import gz.dmndev.restaurant.kitchen.application.port.in.GetKitchenTicketUseCase;
import gz.dmndev.restaurant.kitchen.application.port.in.UpdateTicketStatusUseCase;
import gz.dmndev.restaurant.kitchen.application.port.out.KitchenEventPublisherPort;
import gz.dmndev.restaurant.kitchen.application.port.out.KitchenTicketRepositoryPort;
import gz.dmndev.restaurant.kitchen.domain.exception.KitchenDomainException;
import gz.dmndev.restaurant.kitchen.domain.model.KitchenTicket;
import gz.dmndev.restaurant.kitchen.domain.model.PrepStatus;
import gz.dmndev.restaurant.kitchen.domain.model.TicketItem;
import gz.dmndev.restaurant.kitchen.domain.service.KitchenDomainService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the kitchen application use cases
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KitchenApplicationService implements
        CreateTicketUseCase,
        UpdateTicketStatusUseCase,
        GetKitchenTicketUseCase {

    private final KitchenDomainService kitchenDomainService;
    private final KitchenTicketRepositoryPort kitchenTicketRepository;
    private final KitchenEventPublisherPort kitchenEventPublisher;

    @Override
    @Transactional
    public String createTicket(CreateTicketCommand command) {
        log.info("Creating kitchen ticket for order: {}", command.orderId());

        // Map command items to domain items
        List<TicketItem> items = command.items().stream()
                .map(item -> TicketItem.builder()
                        .productId(item.productId())
                        .productName(item.productName())
                        .quantity(item.quantity())
                        .specialInstructions(item.specialInstructions())
                        .build())
                .collect(Collectors.toList());

        try {
            // Create new ticket using domain service
            KitchenTicket ticket = kitchenDomainService.createTicket(
                    command.orderId(),
                    command.customerId(),
                    command.customerName(),
                    items,
                    command.notes()
            );

            // Set priority if provided
            if (command.priority() != null && command.priority() > 0) {
                ticket.setPriority(command.priority());
            }

            // Save ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(ticket);

            // Publish event
            kitchenEventPublisher.publishTicketCreatedEvent(savedTicket);

            log.info("Kitchen ticket created with ID: {}", savedTicket.getId());
            return savedTicket.getId();
        } catch (KitchenDomainException e) {
            log.error("Failed to create kitchen ticket: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to create kitchen ticket", e);
        }
    }

    @Override
    @Transactional
    public boolean updateTicketStatus(UpdateTicketStatusCommand command) {
        log.info("Updating kitchen ticket status: {} to {}", command.ticketId(), command.newStatus());

        try {
            // Find ticket
            KitchenTicket ticket = kitchenTicketRepository.findById(command.ticketId())
                    .orElseThrow(() -> new KitchenApplicationException("Kitchen ticket not found with ID: " + command.ticketId()));

            // Store old status for event publishing
            PrepStatus oldStatus = ticket.getStatus();

            // Update status
            KitchenTicket updatedTicket = kitchenDomainService.updateTicketStatus(ticket, command.newStatus());

            // Assign to chef if provided and transitioning to IN_PROGRESS
            if (command.chefId() != null && !command.chefId().isBlank() &&
                    command.newStatus() == PrepStatus.IN_PROGRESS) {
                updatedTicket.assignTo(command.chefId());
            }

            // Save updated ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(updatedTicket);

            // Publish event
            kitchenEventPublisher.publishTicketStatusUpdatedEvent(savedTicket, oldStatus, command.newStatus());

            // Publish specific events based on new status
            if (command.newStatus() == PrepStatus.READY) {
                kitchenEventPublisher.publishOrderReadyEvent(savedTicket);
            } else if (command.newStatus() == PrepStatus.CANCELLED) {
                kitchenEventPublisher.publishOrderCancelledEvent(savedTicket);
            }

            log.info("Kitchen ticket status updated: {} from {} to {}",
                    savedTicket.getId(), oldStatus, savedTicket.getStatus());
            return true;
        } catch (Exception e) {
            log.error("Failed to update kitchen ticket status: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to update kitchen ticket status", e);
        }
    }

    @Override
    @Transactional
    public boolean markItemAsPrepared(String ticketId, String productId) {
        log.info("Marking item as prepared: ticket={}, product={}", ticketId, productId);

        try {
            // Find ticket
            KitchenTicket ticket = kitchenTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new KitchenApplicationException("Kitchen ticket not found with ID: " + ticketId));

            // Mark item as prepared
            KitchenTicket updatedTicket = kitchenDomainService.markItemAsPrepared(ticket, productId);

            // Save updated ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(updatedTicket);

            // If status changed to READY, publish order ready event
            if (savedTicket.getStatus() == PrepStatus.READY) {
                kitchenEventPublisher.publishOrderReadyEvent(savedTicket);
            }

            log.info("Item marked as prepared: ticket={}, product={}, progress={}%",
                    ticketId, productId, savedTicket.getPreparationProgress());
            return true;
        } catch (Exception e) {
            log.error("Failed to mark item as prepared: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to mark item as prepared", e);
        }
    }

    @Override
    @Transactional
    public boolean startPreparation(String ticketId, String chefId) {
        log.info("Starting preparation: ticket={}, chef={}", ticketId, chefId);

        try {
            // Find ticket
            KitchenTicket ticket = kitchenTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new KitchenApplicationException("Kitchen ticket not found with ID: " + ticketId));

            // Start preparation
            KitchenTicket updatedTicket = kitchenDomainService.startPreparation(ticket, chefId);

            // Save updated ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(updatedTicket);

            // Publish status updated event
            kitchenEventPublisher.publishTicketStatusUpdatedEvent(
                    savedTicket, PrepStatus.RECEIVED, PrepStatus.IN_PROGRESS);

            log.info("Preparation started: ticket={}, chef={}", ticketId, chefId);
            return true;
        } catch (Exception e) {
            log.error("Failed to start preparation: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to start preparation", e);
        }
    }

    @Override
    @Transactional
    public boolean completePreparation(String ticketId) {
        log.info("Completing preparation: ticket={}", ticketId);

        try {
            // Find ticket
            KitchenTicket ticket = kitchenTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new KitchenApplicationException("Kitchen ticket not found with ID: " + ticketId));

            // Complete preparation
            KitchenTicket updatedTicket = kitchenDomainService.completePreparation(ticket);

            // Save updated ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(updatedTicket);

            // Publish events
            kitchenEventPublisher.publishTicketStatusUpdatedEvent(
                    savedTicket, PrepStatus.IN_PROGRESS, PrepStatus.READY);
            kitchenEventPublisher.publishOrderReadyEvent(savedTicket);

            log.info("Preparation completed: ticket={}", ticketId);
            return true;
        } catch (Exception e) {
            log.error("Failed to complete preparation: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to complete preparation", e);
        }
    }

    @Override
    @Transactional
    public boolean cancelTicket(String ticketId) {
        log.info("Cancelling ticket: {}", ticketId);

        try {
            // Find ticket
            KitchenTicket ticket = kitchenTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new KitchenApplicationException("Kitchen ticket not found with ID: " + ticketId));

            // Store old status for event publishing
            PrepStatus oldStatus = ticket.getStatus();

            // Cancel ticket
            KitchenTicket updatedTicket = kitchenDomainService.cancelTicket(ticket);

            // Save updated ticket
            KitchenTicket savedTicket = kitchenTicketRepository.save(updatedTicket);

            // Publish events
            kitchenEventPublisher.publishTicketStatusUpdatedEvent(
                    savedTicket, oldStatus, PrepStatus.CANCELLED);
            kitchenEventPublisher.publishOrderCancelledEvent(savedTicket);

            log.info("Ticket cancelled: {}", ticketId);
            return true;
        } catch (Exception e) {
            log.error("Failed to cancel ticket: {}", e.getMessage());
            throw new KitchenApplicationException("Failed to cancel ticket", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KitchenTicketDto> getTicketById(String ticketId) {
        log.info("Getting kitchen ticket by ID: {}", ticketId);

        return kitchenTicketRepository.findById(ticketId)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KitchenTicketDto> getTicketByOrderId(String orderId) {
        log.info("Getting kitchen ticket by order ID: {}", orderId);

        return kitchenTicketRepository.findByOrderId(orderId)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenTicketDto> getTicketsByStatus(PrepStatus status) {
        log.info("Getting kitchen tickets by status: {}", status);

        return kitchenTicketRepository.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenTicketDto> getTicketsByChef(String chefId) {
        log.info("Getting kitchen tickets by chef: {}", chefId);

        return kitchenTicketRepository.findByAssignedTo(chefId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenTicketDto> getAllTickets() {
        log.info("Getting all kitchen tickets");

        return kitchenTicketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenStatusSummary getStatusSummary() {
        log.info("Getting kitchen status summary");

        return new KitchenStatusSummary(
                kitchenTicketRepository.countByStatus(PrepStatus.RECEIVED),
                kitchenTicketRepository.countByStatus(PrepStatus.IN_PROGRESS),
                kitchenTicketRepository.countByStatus(PrepStatus.READY),
                kitchenTicketRepository.countByStatus(PrepStatus.DELIVERED),
                kitchenTicketRepository.countByStatus(PrepStatus.CANCELLED),
                kitchenTicketRepository.findAll().size()
        );
    }

    /**
     * Map a domain KitchenTicket to a KitchenTicketDto
     *
     * @param ticket the domain ticket
     * @return the DTO representation
     */
    private KitchenTicketDto mapToDto(KitchenTicket ticket) {
        return new KitchenTicketDto(
                ticket.getId(),
                ticket.getOrderId(),
                ticket.getCustomerId(),
                ticket.getCustomerName(),
                ticket.getItems().stream()
                        .map(item -> new KitchenTicketItemDto(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getSpecialInstructions(),
                                item.isPrepared()
                        ))
                        .collect(Collectors.toList()),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getNotes(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getPreparationStartedAt(),
                ticket.getPreparationCompletedAt(),
                ticket.getAssignedTo(),
                ticket.getPreparationProgress()
        );
    }
}