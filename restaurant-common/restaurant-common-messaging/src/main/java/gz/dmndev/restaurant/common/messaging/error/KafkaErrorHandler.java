package gz.dmndev.restaurant.common.messaging.error;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaErrorHandler implements CommonErrorHandler {

  @Override
  public boolean handleOne(
      Exception thrownException,
      ConsumerRecord<?, ?> record,
      Consumer<?, ?> consumer,
      MessageListenerContainer container) {
    log.error(
        "Error handling Kafka message: Topic={}, Partition={}, Offset={}, Key={}, Value={}",
        record.topic(),
        record.partition(),
        record.offset(),
        record.key(),
        record.value(),
        thrownException);
    return true; // indica que el error fue manejado (y se puede continuar)
  }

  @Override
  public void handleRemaining(
      Exception thrownException,
      List<ConsumerRecord<?, ?>> records,
      Consumer<?, ?> consumer,
      MessageListenerContainer container) {
    log.error(
        "Error handling remaining batch of Kafka messages ({} records)",
        records.size(),
        thrownException);
  }

  @Override
  public void handleOtherException(
      Exception thrownException,
      Consumer<?, ?> consumer,
      MessageListenerContainer container,
      boolean batchListener) {
    log.error(
        "Unrelated error while processing Kafka message (batchListener={})",
        batchListener,
        thrownException);
  }
}
