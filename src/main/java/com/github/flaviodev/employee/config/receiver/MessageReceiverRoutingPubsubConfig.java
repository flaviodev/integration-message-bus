package com.github.flaviodev.employee.config.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import com.github.flaviodev.employee.msgbus.base.pubsub.MessageReceiverRoutingPubsub;

//@Configuration
public class MessageReceiverRoutingPubsubConfig implements MessageReceiverRoutingPubsub {

	@Autowired
	private MessageChannel pubsubOutputChannel;
	
	@Override
	public String getSubscritionName() {
		return "testSubscriptionRouting";
	}

//	@Bean
	public PubSubInboundChannelAdapter userChannelAdapter(@Qualifier("input-user") MessageChannel inputChannel,
			PubSubOperations pubSubTemplate) {
		return getMessageChannelAdapter(inputChannel, pubSubTemplate);
	}

//	@Bean
//	@ServiceActivator(inputChannel = "input-user")
	public MessageHandler userMessageHandler() {
		return message -> receive(message);
	}

	@Override
	public void receive(Message<?> message) {

		final Message<?> messageRouting = MessageBuilder.withPayload(message.getPayload())
				.setHeader(GcpPubSubHeaders.TOPIC, message.getHeaders().get("destinationId")).build();
		
		messageRouting.getHeaders().put("typePayload",  message.getHeaders().get("typePayload"));
		messageRouting.getHeaders().put("operation",  message.getHeaders().get("operation"));
		
		pubsubOutputChannel.send(messageRouting);
	}

}
