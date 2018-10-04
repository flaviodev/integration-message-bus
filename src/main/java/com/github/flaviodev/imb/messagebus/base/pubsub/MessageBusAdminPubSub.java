package com.github.flaviodev.imb.messagebus.base.pubsub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flaviodev.imb.messagebus.base.ActionOnConsumeMessage;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.google.common.collect.ImmutableMap;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
// @Configuration
public class MessageBusAdminPubSub implements MessageBusAdmin {

	private static ObjectMapper mapper;

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Autowired
	private PubSubTemplate pubSubTemplate;

	@Bean
	@Override
	public MessageBusAdmin messageBusAdmin() {
		return this;
	}

	private String getTopicNameWithGroupName(@NonNull String topicName, @NonNull String groupName) {
		if (!groupName.isEmpty())
			topicName = topicName + "-" + groupName;

		return topicName;
	}

	@Override
	public void createTopic(@NonNull String topicName) {
		createTopic(topicName, "");
	}

	@Override
	public void createTopic(@NonNull String topicName, @NonNull String groupName) {
		if (!isRegistredTopic(topicName, groupName)) {
			String topicNameWithGroupName = getTopicNameWithGroupName(topicName, groupName);

			log.info("Creating topic '" + topicNameWithGroupName + "'");
			pubSubAdmin.createTopic(topicNameWithGroupName);
		}
	}

	@Override
	public void deleteTopic(@NonNull String topicName) {
		deleteTopic(topicName, "");
	}

	@Override
	public void deleteTopic(@NonNull String topicName, @NonNull String groupName) {
		if (isRegistredTopic(topicName, groupName)) {
			String topicNameWithGroupName = getTopicNameWithGroupName(topicName, groupName);

			log.info("Deleting topic '" + topicNameWithGroupName + "'");
			pubSubAdmin.deleteTopic(topicNameWithGroupName);
		}
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName) {
		return isRegistredTopic(topicName, "");
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName, @NonNull String groupName) {
		String topicNameWithGroupName = getTopicNameWithGroupName(topicName, groupName);

		return listTopics().stream().filter(topic -> topic.endsWith("topics/" + topicNameWithGroupName)).count() > 0;
	}

	@Override
	public List<String> listTopics() {
		return pubSubAdmin.listTopics().stream().map(Topic::getName).collect(Collectors.toList());
	}

	private String getSubscriptionNameWithGroupName(@NonNull String subscripionName, @NonNull String groupName) {
		if (!groupName.isEmpty())
			subscripionName = subscripionName + "-" + groupName;

		return subscripionName;
	}

	@Override
	public void createSubscriptionForTopic(@NonNull String subscriptionName, @NonNull String topicName) {
		createSubscriptionForTopic(subscriptionName, topicName, "");
	}

	@Override
	public void createSubscriptionForTopic(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull String groupName) {
		createTopic(topicName, groupName);

		if (!isRegistredSubscription(subscriptionName, groupName)) {
			String topicNameWithGroupName = getTopicNameWithGroupName(topicName, groupName);
			String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

			log.info("Creating subscription '" + subscriptionNameWithGroupName + "' for topic '"
					+ topicNameWithGroupName + "'");
			pubSubAdmin.createSubscription(subscriptionNameWithGroupName, topicNameWithGroupName);
		}
	}

	@Override
	public void deleteSubscription(@NonNull String subscriptionName) {
		deleteSubscription(subscriptionName, "");
	}

	@Override
	public void deleteSubscription(@NonNull String subscriptionName, @NonNull String groupName) {
		if (isRegistredSubscription(subscriptionName, groupName)) {
			String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

			log.info("Deleting subscription '" + subscriptionNameWithGroupName + "'");
			pubSubAdmin.deleteSubscription(subscriptionNameWithGroupName);
		}
	}

	@Override
	public List<String> listSubscriptions() {
		return pubSubAdmin.listSubscriptions().stream().map(Subscription::getName).collect(Collectors.toList());
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName) {
		return isRegistredSubscription(subscriptionName, "");
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName, @NonNull String groupName) {
		String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

		return listSubscriptions().stream()
				.filter(inscricao -> inscricao.endsWith("subscriptions/" + subscriptionNameWithGroupName)).count() > 0;
	}

	@Override
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {
		return consumeMessages(subscriptionName, topicName, "", payloadType, action);
	}

	@Override
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull String groupName, @NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		createSubscriptionForTopic(subscriptionName, topicName, groupName);

		String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

		pubSubTemplate.subscribe(subscriptionNameWithGroupName, (message, consumer) -> {
			log.info("Consuming message of the subscription '" + subscriptionNameWithGroupName + "' -> "
					+ message.getData().toStringUtf8());
			consumer.ack();

			action.apply(ImmutableMap.copyOf(message.getAttributesMap()),
					parseJson(payloadType, message.getData().toByteArray()));
		});

		return this;
	}

	@Override
	public <T> void publishMessage(@NonNull String topicName, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		publishMessage(topicName, "", payloadType, payloadObject, headers);
	}

	@Override
	public <T> void publishMessage(@NonNull String topicName, @NonNull String groupName, @NonNull Class<T> payloadType,
			@NonNull T payloadObject, ImmutableMap<String, String> headers) {

		if (headers == null)
			headers = ImmutableMap.of();

		createTopic(topicName, groupName);
		String topicNameWithGroupName = getTopicNameWithGroupName(topicName, groupName);

		String json = stringfyJson(payloadObject);
		log.info("Sending message to topic '" + topicNameWithGroupName + "' ->  " + json);

		Map<String, String> stringHeaders = new HashMap<>();

		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				stringHeaders.put(entry.getKey(), entry.getValue());
			}
		}

		pubSubTemplate.publish(topicNameWithGroupName, json, ImmutableMap.copyOf(stringHeaders));
	}

	private static ObjectMapper getObjectMapper() {
		if (mapper == null)
			mapper = new Jackson2ObjectMapperBuilder().build();

		return mapper;
	}

	@SneakyThrows
	private <T> T parseJson(@NonNull Class<T> payloadType, @NonNull byte[] payload) {
		return getObjectMapper().readValue(payload, payloadType);
	}

	@SneakyThrows
	private String stringfyJson(@NonNull Object payloadObject) {
		return getObjectMapper().writeValueAsString(payloadObject);
	}
}