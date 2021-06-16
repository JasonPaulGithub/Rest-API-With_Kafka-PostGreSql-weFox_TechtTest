package com.wefox.project;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ProjectApplication {

	@Autowired
	private AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

	@EnableKafka
	@Configuration
	public class KafkaConsumerConfig {
		@Bean
		public ConsumerFactory<String, String> consumerFactory() {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
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

	// Step 1: Populate Database

    	JSONObject paymentRecord;
		try {
			paymentRecord = new JSONObject(message);

			Payment payment = new Payment();
			payment.setAccountId((int) paymentRecord.get("account_id"));
			payment.setPaymentId(paymentRecord.get("payment_id").toString());
			payment.setPayment_type(paymentRecord.get("payment_type").toString());
			payment.setCreditCard(paymentRecord.get("credit_card").toString());
			payment.setAmount((int) paymentRecord.get("amount"));
			//payment.setDelay(paymentRecord.get("delay").toString());

			accountRepository.save(payment);

		}catch (JSONException err){
			System.out.println(err.toString());
		}


	// Step 2: Post the log data

		// request url
		String url = "http://localhost:9000/payment";

		// create an instance of RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("payment_id", "John Doe");
		map.put("account_id", "Java Developer");
		map.put("payment_type","test");
		map.put("credit_card","test");
		map.put("amount","test");

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// send POST request
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Payment Request Successful");
		} else {
			System.out.println("Payment Request Failed");
			System.out.println(response.getStatusCode());
		}

	}

}
