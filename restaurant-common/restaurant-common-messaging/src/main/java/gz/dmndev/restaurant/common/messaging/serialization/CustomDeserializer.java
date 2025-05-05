package gz.dmndev.restaurant.common.messaging.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomDeserializer<T> implements Deserializer<T> {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private Class<T> type;

  public CustomDeserializer() {}

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    type = (Class<T>) configs.get("type");
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      if (data == null) {
        return null;
      }
      return objectMapper.readValue(data, type);
    } catch (Exception e) {
      throw new SerializationException("Error deserializing JSON message", e);
    }
  }
}
