package gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging;

import gz.dmndev.restaurant.common.messaging.constants.KafkaTopics;
import gz.dmndev.restaurant.order.application.port.out.OrderEventPublisherPort;
import gz.dmndev.restaurant.order.domain.model.Order;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.event.OrderEventDto;
import gz.dmndev.restaurant.order.infrastructure.adapter.out.messaging.mapper.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {

  private final KafkaTemplate<String, OrderEventDto> kafkaTemplate;
  private final OrderEventMapper mapper;

  @Override
  public void publishOrderCreatedEvent(Order order) {
    OrderEventDto eventDto = mapper.toOrderCreatedEventDto(order);
    kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, order.getId(), eventDto);
  }

  @Override
  public void publishOrderUpdatedEvent(Order order) {
    OrderEventDto eventDto = mapper.toOrderUpdatedEventDto(order);
    kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, order.getId(), eventDto);
  }

  @Override
  public void publishOrderCancelledEvent(Order order) {
    OrderEventDto eventDto = mapper.toOrderCancelledEventDto(order);
    kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, order.getId(), eventDto);
  }
}
