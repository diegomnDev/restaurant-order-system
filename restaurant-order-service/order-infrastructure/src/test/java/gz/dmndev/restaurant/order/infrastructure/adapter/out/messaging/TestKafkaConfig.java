package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging;

import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@SpringBootApplication(
    exclude = {
      KafkaAutoConfiguration.class,
      JpaRepositoriesAutoConfiguration.class,
      HibernateJpaAutoConfiguration.class,
      DataSourceAutoConfiguration.class
    })
public class TestKafkaConfig {

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Bean
  public KafkaTemplate<String, OrderEventDto> kafkaTemplate() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
  }
}
