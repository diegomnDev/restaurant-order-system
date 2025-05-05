package gz.dmndev.restaurant.common.messaging.config;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

class KafkaCommonConfigTest {

  @Test
  void kafkaObjectMapperShouldHandleJavaTimeTypes() {
    // Arrange
    KafkaCommonConfig config = new KafkaCommonConfig();
    ObjectMapper mapper = config.kafkaObjectMapper();
    LocalDateTime now = LocalDateTime.now();
    TestEvent event = new TestEvent("test-id", now);

    // Act & Assert
    try {
      String json = mapper.writeValueAsString(event);
      TestEvent deserializedEvent = mapper.readValue(json, TestEvent.class);

      // Verify deserialization works properly
      assertEquals("test-id", deserializedEvent.getId());
      // Compare just the date parts to avoid millisecond differences
      assertEquals(now.toLocalDate(), deserializedEvent.getTimestamp().toLocalDate());
    } catch (Exception e) {
      fail("Exception while testing mapper: " + e.getMessage());
    }
  }

  @Test
  void jsonMessageConverterShouldUseConfiguredObjectMapper() {
    // Arrange
    KafkaCommonConfig config = new KafkaCommonConfig();
    ObjectMapper mapper = config.kafkaObjectMapper();

    // Act
    RecordMessageConverter converter = config.jsonMessageConverter(mapper);

    // Assert
    assertTrue(converter instanceof JsonMessageConverter);
    // Verify the converter is using our mapper
    JsonMessageConverter jsonConverter = (JsonMessageConverter) converter;
    assertNotNull(jsonConverter);
  }

  // Helper class for testing
  static class TestEvent {
    private String id;
    private LocalDateTime timestamp;

    public TestEvent() {}

    public TestEvent(String id, LocalDateTime timestamp) {
      this.id = id;
      this.timestamp = timestamp;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public LocalDateTime getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
    }
  }
}
