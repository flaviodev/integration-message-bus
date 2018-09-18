package com.github.flaviodev.employee.msgbus.base;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageSenderRouting {

	MessageSenderRouting messageSenderRouting();

	String getTopicName();

	void send(Object destinationId, String oparation, Class<?> typePayload, Object objectPayload) throws JsonProcessingException;
}
