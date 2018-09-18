package com.github.flaviodev.employee.messagebus.receiver;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.github.flaviodev.employee.messagebus.base.MessageProcessor;
import com.github.flaviodev.employee.messagebus.base.pubsub.MessageReceiverPubsub;
import com.github.flaviodev.employee.model.Employee;

//@Configuration
public class MessageReceiverUserPubsubConfig implements MessageReceiverPubsub<Employee> {

	@Override
	public String getSubscritionName() {
		return "testSubscription";
	}
	
//	@Bean
	public PubSubInboundChannelAdapter userChannelAdapter(
			@Qualifier("input-user") MessageChannel inputChannel, PubSubOperations pubSubTemplate) {
		return getMessageChannelAdapter(inputChannel, pubSubTemplate);
	}

//	@Bean
//	@ServiceActivator(inputChannel = "input-user")
	public MessageHandler userMessageHandler() {
		return message -> receive(Employee.class, message);
	}

	@Override
	public MessageProcessor<Employee> getMessageProcessor() {
		return System.out::println;
	}
}
