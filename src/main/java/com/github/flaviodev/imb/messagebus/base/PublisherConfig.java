package com.github.flaviodev.imb.messagebus.base;

public interface PublisherConfig<T> {

	String getTopicName();

	T publishMessage();

	MessageBusAdmin getMessageBusAdmin();
}
