package com.sample.saturn.embedded.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducer {
  private KafkaTemplate<String,String> kafkaTemplate;

  @Autowired
  public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }
  public void send(String topic,String payload){
    kafkaTemplate.send(topic,payload);
  }
}
