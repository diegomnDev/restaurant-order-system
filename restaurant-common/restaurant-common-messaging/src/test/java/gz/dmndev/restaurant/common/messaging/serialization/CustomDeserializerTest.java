package gz.dmndev.restaurant.common.messaging.serialization;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomDeserializerTest {

  private CustomDeserializer<TestData> deserializer;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    deserializer = new CustomDeserializer<>();
    Map<String, Object> configs = new HashMap<>();
    configs.put("type", TestData.class);
    deserializer.configure(configs, false);

    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldDeserializeValidJson() throws JsonProcessingException {
    // Arrange
    TestData testData = new TestData("test-id", "test-value");
    byte[] serialized = objectMapper.writeValueAsBytes(testData);

    // Act
    TestData result = deserializer.deserialize("test-topic", serialized);

    // Assert
    assertNotNull(result);
    assertEquals("test-id", result.getId());
    assertEquals("test-value", result.getValue());
  }

  @Test
  void shouldReturnNullForNullData() {
    // Act
    TestData result = deserializer.deserialize("test-topic", null);

    // Assert
    assertNull(result);
  }

  @Test
  void shouldThrowExceptionForInvalidJson() {
    // Arrange
    byte[] invalidJson = "{invalid-json}".getBytes();

    // Act & Assert
    assertThrows(
        SerializationException.class, () -> deserializer.deserialize("test-topic", invalidJson));
  }

  // Helper class for testing
  static class TestData {
    private String id;
    private String value;

    public TestData() {}

    public TestData(String id, String value) {
      this.id = id;
      this.value = value;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
