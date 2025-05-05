package gz.dmndev.restaurant.common.messaging.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseEvent {
  private String eventId;
  private String eventType;
  private LocalDateTime timestamp;

  protected BaseEvent(String eventId, String eventType) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.timestamp = LocalDateTime.now();
  }
}
