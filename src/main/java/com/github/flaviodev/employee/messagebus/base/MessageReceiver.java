package com.github.flaviodev.employee.messagebus.base;

import org.springframework.messaging.Message;

public interface MessageReceiver<T> {

	String getSubscritionName();

	void receive(Class<T> objectClass, Message<?> message);

	MessageProcessor<T> getMessageProcessor();
}
