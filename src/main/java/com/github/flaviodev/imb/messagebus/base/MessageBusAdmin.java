package com.github.flaviodev.imb.messagebus.base;

import java.util.List;

import com.google.common.collect.ImmutableMap;

public interface MessageBusAdmin {

	void createTopic(String topicName, String groupName);

	void deleteTopic(String topicName, String groupName);

	List<String> listTopics();

	void createSubscriptionForTopic(String subscriptionName, String topicName, String groupName);

	void deleteSubscription(String subscriptionName, String groupName);

	List<String> listSubscriptions();

	boolean isRegistredTopic(String topicName, String groupName);

	boolean isRegistredSubscription(String subscriptionName, String groupName);

	MessageBusAdmin messageBusAdmin();

	<T> MessageBusAdmin consumeMessages(String subscriptionName, String topicName, String groupName,
			Class<T> payloadType, ActionOnConsumeMessage<T> action);

	<T> void publishMessage(String topicName, String groupName, Class<T> payloadType, T payloadObject,
			ImmutableMap<String, String> headers);
}
