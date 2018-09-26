package com.github.flaviodev.employee.messagebus.base;

public interface Receiver {
	String getSubscriptionName();

	String getTopicName();

	Receiver consumeMessage();

	MessageBusAdmin getMessageBusAdmin();
}
