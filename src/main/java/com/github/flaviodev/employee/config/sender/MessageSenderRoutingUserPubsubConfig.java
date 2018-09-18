package com.github.flaviodev.employee.config.sender;

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
import com.github.flaviodev.employee.msgbus.base.MessageSenderRouting;
import com.github.flaviodev.employee.msgbus.base.pubsub.MessageSenderRoutingPubsub;
import com.github.flaviodev.employee.msgbus.base.pubsub.PubSubMessagingGatewayRouting;

//@Configuration
public class MessageSenderRoutingUserPubsubConfig implements MessageSenderRoutingPubsub {

	@Override
	public String getTopicName() {
		return "testTopicRouting";
	}

//	@Bean
//	@ServiceActivator(inputChannel = "output-routing-user")
	public MessageHandler outputUserChannel(PubSubOperations pubsubTemplate) {
		return pubSubMessageHandler(pubsubTemplate);
	}

//	@MessagingGateway(defaultRequestChannel = "output-routing-user")
	public interface UserPubSubMessagingGatewayRouting extends PubSubMessagingGatewayRouting {
	}

	@Bean
	@Primary
	@Override
	public MessageSenderRouting messageSenderRouting() {
		return this;
	}

	@Override
	public void send(Object destinationId, String oparation, Class<?> typePayload, Object payload)
			throws JsonProcessingException {
		UserPubSubMessagingGatewayRouting gateway = SpringContext.getBean(UserPubSubMessagingGatewayRouting.class);

		ObjectMapper mapper = new Jackson2ObjectMapperBuilder().build();
		gateway.sendToPubsub(destinationId, oparation, typePayload, mapper.writeValueAsString(payload));
	}

}
