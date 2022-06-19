package com.sample.saturn.embedded.kafka.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.saturn.embedded.kafka.entity.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Getter
public class KafkaConsumer {

  private final String TOPIC = "testTopic";

  private final ObjectMapper objectMapper;


  private List<Event> events = new ArrayList<>();

  public KafkaConsumer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics=TOPIC)
  public void receive(@Payload String payload, Acknowledgment acknowledgment) throws  Exception{
    log.info("received event : {} ",payload);

    Event event = objectMapper.readValue(payload,Event.class);
    events.add(event);

    //manual commit
    acknowledgment.acknowledge();
  }

  public List<Event> getEvents(){
    return events;
  }
}
