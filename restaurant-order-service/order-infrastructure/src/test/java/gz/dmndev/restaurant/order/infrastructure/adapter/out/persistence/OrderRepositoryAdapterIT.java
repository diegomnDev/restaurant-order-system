package gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.*;

import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.domain.model.OrderItem;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest(classes = {TestJpaConfig.class})
@Testcontainers
class OrderRepositoryAdapterIT {

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:latest")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @Autowired private OrderRepositoryAdapter adapter;

  @Autowired private OrderJpaRepository repository;

  private String orderId;
  private Order testOrder;

  @BeforeEach
  void setUp() {
    // Clear any existing data
    repository.deleteAll();

    orderId = UUID.randomUUID().toString();

    // Crear el item usando el factory method
    OrderItem item =
        OrderItem.createNew(
            UUID.randomUUID().toString(), "prod-1", "Test Product", 2, new BigDecimal("10.00"));

    // Crear la orden usando el builder
    testOrder =
        Order.builder()
            .id(orderId)
            .customerId("cust-1")
            .customerName("Test Customer")
            .items(List.of(item))
            .subtotal(new BigDecimal("20.00"))
            .tax(new BigDecimal("1.60"))
            .total(new BigDecimal("21.60"))
            .status(OrderStatus.CREATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .notes("Test order")
            .build();
  }

  @Test
  void should_SaveAndRetrieveOrder() {
    // Act - Save the order
    Order savedOrder = adapter.save(testOrder);

    // Assert - Check saved order
    assertEquals(orderId, savedOrder.getId());
    assertEquals("Test Customer", savedOrder.getCustomerName());
    assertEquals(OrderStatus.CREATED, savedOrder.getStatus());

    // Act - Retrieve the order
    Optional<Order> retrievedOrderOpt = adapter.findById(orderId);

    // Assert - Check retrieved order
    assertTrue(retrievedOrderOpt.isPresent());
    Order retrievedOrder = retrievedOrderOpt.get();
    assertEquals(orderId, retrievedOrder.getId());
    assertEquals("Test Customer", retrievedOrder.getCustomerName());
    assertEquals(OrderStatus.CREATED, retrievedOrder.getStatus());
    assertEquals(1, retrievedOrder.getItems().size());
    assertEquals("Test Product", retrievedOrder.getItems().get(0).getProductName());
  }

  @Test
  void should_FindOrdersByCustomerId() {
    // Arrange
    adapter.save(testOrder);

    // Create another order for the same customer
    OrderItem item2 =
        OrderItem.createNew(
            UUID.randomUUID().toString(), "prod-2", "Another Product", 1, new BigDecimal("15.00"));

    // Crear segunda orden para el mismo cliente
    Order secondOrder =
        Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId("cust-1") // Same customer
            .customerName("Test Customer")
            .items(List.of(item2))
            .subtotal(new BigDecimal("15.00"))
            .tax(new BigDecimal("1.20"))
            .total(new BigDecimal("16.20"))
            .status(OrderStatus.PAID)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .notes("Second test order")
            .build();

    adapter.save(secondOrder);

    // Act
    List<Order> customerOrders = adapter.findByCustomerId("cust-1");

    // Assert
    assertEquals(2, customerOrders.size());
  }

  @Test
  void should_FindOrdersByStatus() {
    // Arrange
    adapter.save(testOrder);

    // Create another order with different status
    OrderItem item2 =
        OrderItem.createNew(
            UUID.randomUUID().toString(), "prod-2", "Another Product", 1, new BigDecimal("15.00"));

    Order paidOrder =
        Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId("cust-2")
            .customerName("Another Customer")
            .items(List.of(item2))
            .subtotal(new BigDecimal("15.00"))
            .tax(new BigDecimal("1.20"))
            .total(new BigDecimal("16.20"))
            .status(OrderStatus.PAID)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .notes("Paid order")
            .build();

    adapter.save(paidOrder);

    // Act
    List<Order> createdOrders = adapter.findByStatus(OrderStatus.CREATED);
    List<Order> paidOrders = adapter.findByStatus(OrderStatus.PAID);

    // Assert
    assertEquals(1, createdOrders.size());
    assertEquals(1, paidOrders.size());
    assertEquals(OrderStatus.CREATED, createdOrders.get(0).getStatus());
    assertEquals(OrderStatus.PAID, paidOrders.get(0).getStatus());
  }

  @Test
  void should_UpdateExistingOrder() {
    // Arrange
    adapter.save(testOrder);

    // En lugar de crear una nueva orden, actualizamos la existente
    Order orderToUpdate = adapter.findById(orderId).get();
    orderToUpdate.updateStatus(OrderStatus.PAID);
    orderToUpdate.updateNotes("Updated notes");

    // Act
    Order result = adapter.save(orderToUpdate);

    // Assert
    assertEquals(OrderStatus.PAID, result.getStatus());
    assertEquals("Updated notes", result.getNotes());

    // Verify from repository
    Optional<Order> fromRepo = adapter.findById(orderId);
    assertTrue(fromRepo.isPresent());
    assertEquals(OrderStatus.PAID, fromRepo.get().getStatus());
    assertEquals("Updated notes", fromRepo.get().getNotes());
  }
}
