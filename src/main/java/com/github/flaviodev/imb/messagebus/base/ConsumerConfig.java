package com.github.flaviodev.imb.messagebus.base;

public interface ConsumerConfig<T> {
	String getSubscriptionName();

	String getTopicName();

	String getGroupName();

	Class<T> getPayloadType();

	ActionOnConsumeMessage<T> getActionOnConsumeMessage();

	ConsumerConfig<T> consumeMessage();

	MessageBusAdmin getMessageBusAdmin();

	default void doConsumeMessage() {
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), getGroupName(), getPayloadType(),
				getActionOnConsumeMessage());
	}
}
