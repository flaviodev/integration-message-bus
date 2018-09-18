package com.github.flaviodev.employee.msgbus.base;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageSender<T> {
	
	MessageSender<T> messageSender();
	
	String getTopicName();
	
	void send(T objectPayload) throws JsonProcessingException;
}
