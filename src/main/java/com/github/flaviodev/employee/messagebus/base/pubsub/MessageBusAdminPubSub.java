package com.github.flaviodev.employee.messagebus.base.pubsub;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flaviodev.employee.messagebus.base.ActionOnConsumeMessage;
import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageSubscription;
import com.github.flaviodev.employee.messagebus.base.MessageTopic;
import com.google.common.collect.ImmutableMap;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class MessageBusAdminPubSub implements MessageBusAdmin {

	private static ObjectMapper mapper;

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Autowired
	private PubSubTemplate pubSubTemplate;

	@Override
	public void createTopic(@NonNull String topicName) {
		if (!isRegistredTopic(topicName))
			pubSubAdmin.createTopic(topicName);
	}

	@Override
	public void deleteTopic(@NonNull String topicName) {
		if (isRegistredTopic(topicName))
			pubSubAdmin.deleteTopic(topicName);
	}

	@Override
	public List<String> listTopics() {
		return pubSubAdmin.listTopics().stream().map(Topic::getName).collect(Collectors.toList());
	}

	@Override
	public void createSubscription(@NonNull String subscriptionName, @NonNull String topicName) {
		createTopic(topicName);

		if (!isRegistredSubscription(subscriptionName))
			pubSubAdmin.createSubscription(subscriptionName, topicName);
	}

	@Override
	public void deleteSubscription(@NonNull String subscriptionName) {
		if (isRegistredSubscription(subscriptionName))
			pubSubAdmin.deleteSubscription(subscriptionName);
	}

	@Override
	public List<String> listSubscriptions() {
		return pubSubAdmin.listSubscriptions().stream().map(Subscription::getName).collect(Collectors.toList());
	}

	@Bean
	@Primary
	@Override
	public MessageBusAdmin messageBusAdmin() {
		return this;
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName) {
		return listTopics().stream().filter(topico -> topico.endsWith("topics/" + topicName)).count() > 0;
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName) {
		return listSubscriptions().stream().filter(inscricao -> inscricao.endsWith("subscriptions/" + subscriptionName))
				.count() > 0;
	}

	@Override
	public <T> MessageBusAdmin consumeMessages(@NonNull MessageSubscription subscription, @NonNull Class<T> payloadType,
			@NonNull ActionOnConsumeMessage<T> action) {
		return consumeMessages(subscription.getName(), subscription.getTopicName(), payloadType, action);
	}

	@Override
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		createSubscription(subscriptionName, topicName);

		pubSubTemplate.subscribe(subscriptionName, (message, consumer) -> {
			log.info("consuming message [" + subscriptionName + "]: " + message.getData().toStringUtf8());
			consumer.ack();

			action.apply(ImmutableMap.copyOf(message.getAttributesMap()),
					parseJson(payloadType, message.getData().toByteArray()));
		});

		return this;
	}

	@Override
	public <T> void sendMessage(@NonNull MessageTopic topic, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		sendMessage(topic.getName(), payloadType, payloadObject, headers);
	}

	@Override
	public <T> void sendMessage(@NonNull String topicName, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		if (headers == null)
			headers = ImmutableMap.of();

		createTopic(topicName);

		String json = stringfyJson(payloadObject);
		log.info("Sending message [" + topicName + "]: " + json);
		pubSubTemplate.publish(topicName, json, headers);
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
