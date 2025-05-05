package gz.dmndev.restaurant.common.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaCommonConfig {

  @Bean
  public ObjectMapper kafkaObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  @Bean
  public RecordMessageConverter jsonMessageConverter(ObjectMapper kafkaObjectMapper) {
    return new JsonMessageConverter(kafkaObjectMapper);
  }
}
