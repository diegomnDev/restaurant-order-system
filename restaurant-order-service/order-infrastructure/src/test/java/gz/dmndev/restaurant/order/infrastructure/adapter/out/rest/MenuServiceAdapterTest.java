package gz.dmndev.restaurant.order.infrastructure.adapter.out.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import gz.dmndev.restaurant.order.application.port.out.MenuServicePort.ProductInfo;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class MenuServiceAdapterTest {

  @Mock private MenuServiceClient menuServiceClient;

  @InjectMocks private MenuServiceAdapter adapter;

  @Test
  void getProduct_shouldReturnProductInfo_whenProductExists() {
    // Arrange
    String productId = "prod-1";
    MenuServiceClient.ProductResponse response =
        new MenuServiceClient.ProductResponse(
            productId, "Test Product", new BigDecimal("10.00"), true);
    when(menuServiceClient.getProduct(productId)).thenReturn(response);

    // Act
    Optional<ProductInfo> result = adapter.getProduct(productId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(productId, result.get().id());
    assertEquals("Test Product", result.get().name());
    assertEquals(new BigDecimal("10.00"), result.get().price());
    assertTrue(result.get().available());
  }

  @Test
  void getProduct_shouldReturnEmpty_whenExceptionOccurs() {
    // Arrange
    String productId = "prod-error";
    when(menuServiceClient.getProduct(productId))
        .thenThrow(new RestClientException("Service unavailable"));

    // Act
    Optional<ProductInfo> result = adapter.getProduct(productId);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  void getProduct_shouldHandleNullResponse() {
    // Arrange
    String productId = "prod-null";
    when(menuServiceClient.getProduct(productId)).thenReturn(null);

    // Act
    Optional<ProductInfo> result = adapter.getProduct(productId);

    // Assert
    assertTrue(result.isEmpty());
  }
}
