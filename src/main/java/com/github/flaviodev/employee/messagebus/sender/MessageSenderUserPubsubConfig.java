package com.github.flaviodev.employee.messagebus.sender;

import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flaviodev.employee.SpringContext;
import com.github.flaviodev.employee.messagebus.base.pubsub.MessageSenderPubsub;
import com.github.flaviodev.employee.messagebus.base.pubsub.PubSubMessagingGateway;
import com.github.flaviodev.employee.model.Employee;

//@Configuration
public class MessageSenderUserPubsubConfig implements MessageSenderUser, MessageSenderPubsub<Employee> {

	@Override
	public String getTopicName() {
		return "testTopic";
	}

	@Override
	public void send(Employee user) throws JsonProcessingException {
		UserPubSubMessagingGateway gateway = SpringContext.getBean(UserPubSubMessagingGateway.class);

		ObjectMapper mapper = new Jackson2ObjectMapperBuilder().build();
		gateway.sendToPubsub(mapper.writeValueAsString(user));
	}

//	@Bean
//	@ServiceActivator(inputChannel = "output-user")
	public MessageHandler outputUserChannel(PubSubOperations pubsubTemplate) {
		return pubSubMessageHandler(pubsubTemplate);
	}

//	@MessagingGateway(defaultRequestChannel = "output-user")
	public interface UserPubSubMessagingGateway extends PubSubMessagingGateway {
	}

//	@Bean
//	@Primary
//	@Override
	public MessageSenderUser messageSender() {
		return this;
	}

}
