package com.github.flaviodev.employee.messagebus.base;

import org.springframework.messaging.Message;

public interface MessageReceiverRouting {

	String getSubscritionName();

	void receive(Message<?> message);
}
