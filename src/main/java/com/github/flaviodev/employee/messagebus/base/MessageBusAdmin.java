package com.github.flaviodev.employee.messagebus.base;

import java.util.List;

import com.google.common.collect.ImmutableMap;

public interface MessageBusAdmin {

	void createTopic(String topicName);

	void deleteTopic(String topicName);

	List<String> listTopics();

	void criarInscricao(String subscriptionName, String topicName);

	void deleteSubscription(String subscriptionName);

	List<String> listSubscriptions();

	boolean isRegistredTopic(String topicName);

	boolean isRegistredSubscription(String subscriptionName);

	void verifySubscription(String subscriptionName, String topicName);

	MessageBusAdmin messageBusAdmin();

	<T> MessageBusAdmin consumeMessages(String subscriptionName, String topicName, Class<T> payloadType,
			ActionOnConsumeMessage<T> action);

	<T> MessageBusAdmin consumeMessages(MessageSubscription subscription, Class<T> payloadType,
			ActionOnConsumeMessage<T> action);

	<T> void sendMessage(String topicName, Class<T> payloadType, T payloadObject, ImmutableMap<String, String> headers);

	<T> void sendMessage(MessageTopic topico, Class<T> payloadType, T payloadObject,
			ImmutableMap<String, String> headers);
}
