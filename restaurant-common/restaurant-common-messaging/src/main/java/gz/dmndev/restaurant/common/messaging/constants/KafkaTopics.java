package gz.dmndev.restaurant.common.messaging.constants;

public class KafkaTopics {
  public static final String ORDER_EVENTS = "order-events";
  public static final String KITCHEN_EVENTS = "kitchen-events";
  public static final String DELIVERY_EVENTS = "delivery-events";
  public static final String NOTIFICATION_EVENTS = "notification-events";

  private KafkaTopics() {}
}
