package com.wefox.project;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ProjectApplication {

    private final static String TOPIC = "online";
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

	@EnableKafka
	@Configuration
	public class KafkaConsumerConfig {
		@Bean
		public ConsumerFactory<String, String> consumerFactory() {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			return new DefaultKafkaConsumerFactory<>(props);
		}
		@Bean
		public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
			ConcurrentKafkaListenerContainerFactory<String, String>
					factory = new ConcurrentKafkaListenerContainerFactory<>();
			factory.setConsumerFactory(consumerFactory());
			return factory;
		}
	}

	@KafkaListener(topics = {"online" , "offline"}, groupId = "group-id")
	public void listen(String message) {
		System.out.println("Received message : " + message);
	}

}
