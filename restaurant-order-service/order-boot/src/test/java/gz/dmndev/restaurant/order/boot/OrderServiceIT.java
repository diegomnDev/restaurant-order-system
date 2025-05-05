package gz.dmndev.restaurant.order.boot;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import gz.dmndev.restaurant.order.domain.model.OrderStatus;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.CreateOrderRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderItemRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.OrderResponse;
import gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.dto.UpdateStatusRequest;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.rest.MenuServiceClient;
import gz.dmndev.restaurant.order.infrastructure.config.FeignConfig;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = OrderBootApplication.class)
@EntityScan(
    basePackages = "gz.dmndev.restaurant.order.infrastructure.adapter.out.persistence.entity")
@Testcontainers
@ContextConfiguration(
    classes = {OrderServiceIT.FeignTestConfiguration.class, MenuServiceClient.class})
@EnableWireMock({
  @ConfigureWireMock(name = "menu-service", baseUrlProperties = "restaurant.menu-service.url")
})
class OrderServiceIT {

  @Autowired private TestRestTemplate restTemplate;

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:latest")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @InjectWireMock("menu-service")
  private WireMockServer wiremock;

  @Autowired private ClienteFeingTest menuServiceClient;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    registry.add("spring.kafka.enabled", () -> "false");
  }

  @BeforeEach
  void setUp() {

    wiremock.stubFor(
        WireMock.get(urlEqualTo("/menu-items/prod-1"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                 {
                                                                   "id": "prod-1",
                                                                   "name": "Pizza Margherita",
                                                                   "price": 10.00,
                                                                   "available": true
                                                                 }
                                                                """)));

    wiremock.stubFor(
        WireMock.get(urlEqualTo("/menu-items/prod-2"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                 {
                                                                   "id": "prod-2",
                                                                   "name": "Pizza Pepperoni",
                                                                   "price": 12.00,
                                                                   "available": true
                                                                 }
                                                                """)));
  }

  @Test
  void createOrder_shouldReturnCreatedOrder() {
    // Arrange
    CreateOrderRequest request =
        new CreateOrderRequest(
            "cust-1",
            "John Doe",
            List.of(new OrderItemRequest("prod-1", 2), new OrderItemRequest("prod-2", 1)),
            "Deliver ASAP");

    // Act
    ResponseEntity<OrderResponse> response =
        restTemplate.postForEntity("/orders", request, OrderResponse.class);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    OrderResponse orderResponse = response.getBody();
    assertNotNull(orderResponse);
    assertNotNull(orderResponse.id());
    assertEquals("cust-1", orderResponse.customerId());
    assertEquals("John Doe", orderResponse.customerName());
    assertEquals(2, orderResponse.items().size());
    assertEquals(OrderStatus.CREATED, orderResponse.status());

    var pizzaMargherita =
        orderResponse.items().stream()
            .filter(item -> item.productId().equals("prod-1"))
            .findFirst()
            .orElseThrow();
    assertEquals("Pizza Margherita", pizzaMargherita.productName());
    assertEquals(2, pizzaMargherita.quantity());
    assertEquals(new BigDecimal("10.00"), pizzaMargherita.unitPrice());
    assertEquals(new BigDecimal("20.00"), pizzaMargherita.totalPrice());

    var pizzaPepperoni =
        orderResponse.items().stream()
            .filter(item -> item.productId().equals("prod-2"))
            .findFirst()
            .orElseThrow();
    assertEquals("Pizza Pepperoni", pizzaPepperoni.productName());
    assertEquals(1, pizzaPepperoni.quantity());
    assertEquals(new BigDecimal("12.00"), pizzaPepperoni.unitPrice());
    assertEquals(new BigDecimal("12.00"), pizzaPepperoni.totalPrice());

    assertEquals(new BigDecimal("32.00"), orderResponse.subtotal());
    assertEquals(new BigDecimal("3.2000"), orderResponse.tax());
    assertEquals(new BigDecimal("35.2000"), orderResponse.total());
  }

  @Test
  void getOrderById_shouldReturnOrder_whenOrderExists() {
    // Arrange
    CreateOrderRequest createRequest =
        new CreateOrderRequest(
            "cust-1", "John Doe", List.of(new OrderItemRequest("prod-1", 1)), "Test order");

    ResponseEntity<OrderResponse> createResponse =
        restTemplate.postForEntity("/orders", createRequest, OrderResponse.class);
    String orderId = createResponse.getBody().id();

    // Act
    ResponseEntity<OrderResponse> response =
        restTemplate.getForEntity("/orders/" + orderId, OrderResponse.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponse orderResponse = response.getBody();
    assertNotNull(orderResponse);
    assertEquals(orderId, orderResponse.id());
    assertEquals("cust-1", orderResponse.customerId());
  }

  @Test
  void getOrderById_shouldReturnNotFound_whenOrderDoesNotExist() {
    // Act
    ResponseEntity<OrderResponse> response =
        restTemplate.getForEntity("/orders/non-existent-id", OrderResponse.class);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void updateOrderStatus_shouldUpdateStatus() {
    // Arrange
    CreateOrderRequest createRequest =
        new CreateOrderRequest(
            "cust-1", "John Doe", List.of(new OrderItemRequest("prod-1", 1)), "Test order");

    ResponseEntity<OrderResponse> createResponse =
        restTemplate.postForEntity("/orders", createRequest, OrderResponse.class);
    String orderId = createResponse.getBody().id();

    UpdateStatusRequest updateRequest = new UpdateStatusRequest(OrderStatus.PAID);

    // Act
    ResponseEntity<OrderResponse> response =
        restTemplate.exchange(
            "/orders/" + orderId + "/status",
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest),
            OrderResponse.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponse updatedOrder = response.getBody();
    assertNotNull(updatedOrder);
    assertEquals(OrderStatus.PAID, updatedOrder.status());
  }

  @Test
  void cancelOrder_shouldCancelOrder() {
    // Arrange
    CreateOrderRequest createRequest =
        new CreateOrderRequest(
            "cust-1", "John Doe", List.of(new OrderItemRequest("prod-1", 1)), "Test order");

    ResponseEntity<OrderResponse> createResponse =
        restTemplate.postForEntity("/orders", createRequest, OrderResponse.class);
    String orderId = createResponse.getBody().id();

    // Act
    ResponseEntity<OrderResponse> response =
        restTemplate.exchange(
            "/orders/" + orderId + "/cancel", HttpMethod.PUT, null, OrderResponse.class);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    OrderResponse cancelledOrder = response.getBody();
    assertNotNull(cancelledOrder);
    assertEquals(OrderStatus.CANCELLED, cancelledOrder.status());
  }

  @Configuration
  @EnableFeignClients
  static class FeignTestConfiguration {}

  @Profile("test")
  @FeignClient(
      name = "menu-service",
      url = "${restaurant.menu-service.url}",
      configuration = FeignConfig.class)
  interface ClienteFeingTest extends MenuServiceClient {}
}
