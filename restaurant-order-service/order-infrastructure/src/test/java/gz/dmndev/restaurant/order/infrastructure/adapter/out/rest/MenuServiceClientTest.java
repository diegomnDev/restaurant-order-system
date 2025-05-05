package gz.dmndev.restaurant.order.infrastructure.adapter.out.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import gz.dmndev.restaurant.order.infrastructure.config.FeignConfig;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(
    classes = {MenuServiceClientTest.FeignTestConfiguration.class, MenuServiceClient.class})
@EnableWireMock({
  @ConfigureWireMock(name = "menu-service", baseUrlProperties = "restaurant.menu-service.url")
})
class MenuServiceClientTest {

  @InjectWireMock("menu-service")
  private WireMockServer wiremock;

  @Autowired private ClienteFeingTest menuServiceClient;

  @Test
  void getProduct_shouldReturnProduct_whenProductExists() {
    // Arrange
    String productId = "prod-1";
    wiremock.stubFor(
        get(urlEqualTo("/menu-items/" + productId))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(
                        """
                        {
                            "id": "prod-1",
                            "name": "Test Product",
                            "price": 10.00,
                            "available": true
                        }
                        """)));

    // Act
    MenuServiceClient.ProductResponse response = menuServiceClient.getProduct(productId);

    // Assert
    assertNotNull(response);
    assertEquals("prod-1", response.id());
    assertEquals("Test Product", response.name());
    assertEquals(new BigDecimal("10.00"), response.price());
    assertTrue(response.available());
  }

  @Test
  void getProduct_shouldThrowException_whenProductNotFound() {
    // Arrange
    String productId = "non-existent";
    wiremock.stubFor(
        get(urlEqualTo("/menu-items/" + productId))
            .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> menuServiceClient.getProduct(productId));
  }

  @Test
  void getProduct_shouldThrowException_whenServerError() {
    // Arrange
    String productId = "error-product";
    wiremock.stubFor(
        get(urlEqualTo("/menu-items/" + productId))
            .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> menuServiceClient.getProduct(productId));
  }

  @Configuration
  @EnableFeignClients
  @ImportAutoConfiguration({
    FeignAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class
  })
  static class FeignTestConfiguration {

    @Bean
    public HttpMessageConverters httpMessageConverters() {
      return new HttpMessageConverters(
          new MappingJackson2HttpMessageConverter() // Para JSON
          );
    }
  }

  @Profile("test")
  @FeignClient(
      name = "menu-service",
      url = "${restaurant.menu-service.url}",
      configuration = FeignConfig.class)
  interface ClienteFeingTest extends MenuServiceClient {}
}
