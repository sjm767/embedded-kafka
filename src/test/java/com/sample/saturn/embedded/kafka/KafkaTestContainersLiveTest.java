package com.sample.saturn.embedded.kafka;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.saturn.embedded.kafka.consumer.KafkaConsumer;
import com.sample.saturn.embedded.kafka.entity.Event;
import com.sample.saturn.embedded.kafka.producer.KafkaProducer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@Import(KafkaTestContainersLiveTest.KafkaTestContainersConfiguration.class)
@SpringBootTest

public class KafkaTestContainersLiveTest {

  //kafkaContainer를 docker-container이미지로 지정함.
  //Rule 지정을 통해 테스트 과정에서 Kafka 컨테이너의 수명주기를 관리한다.
  @ClassRule
  public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2"));

  @Autowired
  public KafkaTemplate<String,String> testKafkaTemplate;

  @Autowired
  private KafkaConsumer consumer;

  @Autowired
  private KafkaProducer producer;

  @Autowired
  ObjectMapper objectMapper;

  private String topic="testTopic";

  @Test
  public void test() throws Exception{
    Event event = Event.builder()
        .packNo(1L)
        .controNo(11L)
        .ItemName("testItem")
        .build();
    String payload = objectMapper.writeValueAsString(event);
    producer.send(topic,payload);

    Thread.sleep(5000);

    assertNotEquals(0,consumer.getEvents().size());
  }


  /**
   *  별도의 Configuration을 정의하는 이유: Docker Container가 시작될 때 동적으로 서버이름이 할당되는데, 이 때 동적으로 생성되는 서버 주소를 주입할 필요가 있다고 함
   *  밑의 getBootstrapServers()를 활용해서 동적으로 생성되는 서버주소를 알아온다.
   *  이는 기존 Configuration 코드와 다르므로 테스트용으로 별도 선언하는 것.
   */
  @TestConfiguration
  static class KafkaTestContainersConfiguration {

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> testKafkaListenerContainerFactory() {
      ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
      factory.setConsumerFactory(testConsumerFactory());
      return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> testConsumerFactory() {
      return new DefaultKafkaConsumerFactory<>(testConsumerConfigs());
    }

    @Bean
    public Map<String, Object> testConsumerConfigs() {
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "jaeshim-testcontainer-group");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      return props;
    }

    @Bean
    public ProducerFactory<String, String> testProducerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> testKafkaTemplate() {
      return new KafkaTemplate<>(testProducerFactory());
    }

  }

}
