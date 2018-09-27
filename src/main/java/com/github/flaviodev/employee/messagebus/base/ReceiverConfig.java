package com.github.flaviodev.employee.messagebus.base;

public interface ReceiverConfig {
	String getSubscriptionName();

	String getTopicName();

	ReceiverConfig consumeMessage();

	MessageBusAdmin getMessageBusAdmin();
}
