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

		// Received message : {"payment_id": "17824764-652f-4556-9365-51b903554086", "account_id": 616, "payment_type": "online", "credit_card": "503893034499", "amount": 32, "delay": 187}

		// Step 1 : Send data to DB (make test? use ORM?)

		// Step 2 : Send data to external payment API via REST

		// Process and outcomes of both steps will need to be logged. (try/catch both steps?)

	}

}
