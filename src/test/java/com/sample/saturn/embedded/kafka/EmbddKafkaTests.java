package com.sample.saturn.embedded.kafka;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.saturn.embedded.kafka.consumer.KafkaConsumer;
import com.sample.saturn.embedded.kafka.entity.Event;
import com.sample.saturn.embedded.kafka.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
            "listeners=PLAINTEXT://localhost:9092"
        },
        ports = {9092})
class EmbddKafkaTests {

  @Autowired
  KafkaProducer producer;

	@Autowired
	KafkaConsumer consumer;

	@Autowired
	ObjectMapper objectMapper;

	String topic = "testTopic";

	@Test
	void test() throws Exception{
		Event event = Event.builder()
				.packNo(1L)
				.controNo(11L)
				.ItemName("testItem")
				.build();
		String payload = objectMapper.writeValueAsString(event);
		producer.send(topic,payload);

		Thread.sleep(2000);

		assertNotEquals(0,consumer.getEvents().size());

	}

}
