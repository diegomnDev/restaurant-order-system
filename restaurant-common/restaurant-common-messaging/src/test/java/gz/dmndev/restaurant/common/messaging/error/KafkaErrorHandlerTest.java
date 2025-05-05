package gz.dmndev.restaurant.common.messaging.error;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.MessageListenerContainer;

@ExtendWith(MockitoExtension.class)
class KafkaErrorHandlerTest {

  private KafkaErrorHandler errorHandler;

  @Mock private Consumer<String, Object> consumer;

  @Mock private MessageListenerContainer container;

  @Mock private ConsumerRecord<String, Object> record;

  @BeforeEach
  void setUp() {
    errorHandler = new KafkaErrorHandler();
  }

  @Test
  void handleOneShouldNotThrowException() {
    // Arrange
    Exception testException = new RuntimeException("Test exception");

    // Act & Assert - Verify no exceptions are thrown
    assertDoesNotThrow(() -> errorHandler.handleOne(testException, record, consumer, container));
  }

  @Test
  void handleRemainingShouldNotThrowException() {
    // Arrange
    Exception testException = new RuntimeException("Test exception");
    List<ConsumerRecord<?, ?>> records = Collections.singletonList(record);

    // Act & Assert - Verify no exceptions are thrown
    assertDoesNotThrow(
        () -> errorHandler.handleRemaining(testException, records, consumer, container));
  }

  @Test
  void handleAllShouldNotThrowException() {
    // Arrange
    Exception testException = new RuntimeException("Test exception");
    boolean batchListener = true;

    // Act & Assert - Verify no exceptions are thrown
    assertDoesNotThrow(
        () -> errorHandler.handleOtherException(testException, consumer, container, batchListener));
  }
}
