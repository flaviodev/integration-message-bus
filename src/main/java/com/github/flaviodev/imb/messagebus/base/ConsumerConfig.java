package com.github.flaviodev.imb.messagebus.base;

public interface ConsumerConfig {
	String getSubscriptionName();

	String getTopicName();
	
	String getGroupName();

	ConsumerConfig consumeMessage();

	MessageBusAdmin getMessageBusAdmin();
}
