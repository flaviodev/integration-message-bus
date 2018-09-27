package com.github.flaviodev.employee.messagebus.base;

public interface SenderConfig<T> {

	String getTopicName();

	T sendMessage();

	MessageBusAdmin getMessageBusAdmin();
}
