package com.sample.saturn.embedded.kafka.config;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
public class KafkaConsumerConfig {
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String AUTO_OFFSET_REST;

  @Value("${spring.kafka.consumer.bootstrap-servers}")
  private String BOOTSTRAP_SERVERS;

  @Value("${spring.kafka.consumer.enable-auto-commit}")
  private String AUTO_COMMIT;

  @Value("${spring.kafka.consumer.group-id}")
  private String GROUP_ID;

  @Bean
  ConsumerFactory<String,String> consumerFactory(){
    Map<String,Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,AUTO_OFFSET_REST);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,AUTO_COMMIT);
    props.put(ConsumerConfig.GROUP_ID_CONFIG,GROUP_ID);

    return new DefaultKafkaConsumerFactory<>(props,new StringDeserializer(),new StringDeserializer());
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String,String> containerFactory(){
    ConcurrentKafkaListenerContainerFactory<String,String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
