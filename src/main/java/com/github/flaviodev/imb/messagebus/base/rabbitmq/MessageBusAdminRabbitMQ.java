package com.github.flaviodev.imb.messagebus.base.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitManagementTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flaviodev.imb.messagebus.base.ActionOnConsumeMessage;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class MessageBusAdminRabbitMQ implements MessageBusAdmin {

	private static ObjectMapper mapper;

	private static RabbitManagementTemplate managementTemplate;

	private RabbitManagementTemplate getRabbitManagementTemplate() {

		if (managementTemplate == null)
			managementTemplate = new RabbitManagementTemplate();

		return managementTemplate;
	}

	private static Channel channel;

	@SneakyThrows
	private Channel getChannel() {

		if (channel == null) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			channel = factory.newConnection().createChannel();
		}

		return channel;
	}

	@Bean
	@Override
	public MessageBusAdmin messageBusAdmin() {
		return this;
	}

	@Override
	@SneakyThrows
	public void createTopic(@NonNull String topicName) {
		createTopic(topicName, "");
	}

	@Override
	@SneakyThrows
	public void createTopic(@NonNull String topicName, @NonNull String groupName) {
		if (!isRegistredTopic(topicName, groupName)) {
			log.info("Creating topic '" + topicName + "'");
			getChannel().exchangeDeclare(topicName, "direct");
		}
	}

	@Override
	@SneakyThrows
	public void deleteTopic(@NonNull String topicName) {
		deleteTopic(topicName, "");
	}

	@Override
	@SneakyThrows
	public void deleteTopic(@NonNull String topicName, @NonNull String groupName) {
		if (isRegistredTopic(topicName, groupName)) {
			log.info("Deleting topic '" + topicName + "'");
			getChannel().exchangeDelete(topicName);
		}
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName) {
		return isRegistredTopic(topicName, "");
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName, @NonNull String groupName) {
		return listTopics().stream().filter(topic -> topic.equals(topicName)).count() > 0;
	}

	@Override
	public List<String> listTopics() {
		return getRabbitManagementTemplate().getExchanges().stream().map(Exchange::getName)
				.collect(Collectors.toList());
	}

	private String getSubscriptionNameWithGroupName(@NonNull String subscripionName, @NonNull String groupName) {
		if (!groupName.isEmpty())
			subscripionName = subscripionName + "-" + groupName;

		return subscripionName;
	}

	@Override
	@SneakyThrows
	public void createSubscriptionForTopic(@NonNull String subscriptionName, @NonNull String topicName) {
		createSubscriptionForTopic(subscriptionName, topicName, "");
	}

	@Override
	@SneakyThrows
	public void createSubscriptionForTopic(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull String groupName) {
		createTopic(topicName, groupName);

		if (!isRegistredSubscription(subscriptionName, groupName)) {
			String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

			log.info("Creating subscription '" + subscriptionNameWithGroupName + "' for topic '" + topicName + "'"
					+ (!groupName.isEmpty() ? "' with reoutingKey '" + groupName + "'" : ""));
			getChannel().queueDeclare(subscriptionNameWithGroupName, true, false, false, null);
			getChannel().queueBind(subscriptionNameWithGroupName, topicName, groupName);
		}
	}

	@Override
	@SneakyThrows
	public void deleteSubscription(@NonNull String subscriptionName) {
		deleteSubscription(subscriptionName, "");
	}

	@Override
	@SneakyThrows
	public void deleteSubscription(@NonNull String subscriptionName, @NonNull String groupName) {
		if (isRegistredSubscription(subscriptionName, groupName)) {
			String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

			log.info("Deleting subscription '" + subscriptionNameWithGroupName + "'");
			getChannel().queueDelete(subscriptionNameWithGroupName);
		}
	}

	@Override
	public List<String> listSubscriptions() {
		return getRabbitManagementTemplate().getQueues().stream().map(Queue::getName).collect(Collectors.toList());
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName) {
		return isRegistredSubscription(subscriptionName, "");
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName, @NonNull String groupName) {
		String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

		return listSubscriptions().stream().filter(subscription -> subscription.equals(subscriptionNameWithGroupName))
				.count() > 0;
	}

	@Override
	@SneakyThrows
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		return consumeMessages(subscriptionName, topicName, "", payloadType, action);

	}

	@Override
	@SneakyThrows
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull String groupName, @NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		createSubscriptionForTopic(subscriptionName, topicName, groupName);

		String subscriptionNameWithGroupName = getSubscriptionNameWithGroupName(subscriptionName, groupName);

		Consumer consumer = new DefaultConsumer(getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				String message = new String(body, "UTF-8");

				log.info("Consuming message of the subscription '" + subscriptionNameWithGroupName + "' -> " + message);

				Map<String, String> headers = new HashMap<>();

				for (Entry<String, Object> entry : properties.getHeaders().entrySet()) {
					headers.put(entry.getKey(), entry.getValue().toString());
				}

				action.apply(ImmutableMap.copyOf(headers), parseJson(payloadType, body));
			}
		};
		getChannel().basicConsume(subscriptionNameWithGroupName, true, consumer);
		return this;
	}

	@Override
	@SneakyThrows
	public <T> void publishMessage(@NonNull String topicName, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		publishMessage(topicName, "", payloadType, payloadObject, headers);
	}

	@Override
	@SneakyThrows
	public <T> void publishMessage(@NonNull String topicName, @NonNull String groupName, @NonNull Class<T> payloadType,
			@NonNull T payloadObject, ImmutableMap<String, String> headers) {
		createTopic(topicName, groupName);

		String json = stringfyJson(payloadObject);

		Map<String, Object> headerMap = new HashMap<>();

		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				headerMap.put(entry.getKey(), entry.getValue());
			}
		}
		AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().headers(headerMap).build();

		getChannel().basicPublish(topicName, groupName, basicProperties, json.getBytes());

		log.info("Sending message to topic '" + topicName
				+ (!groupName.isEmpty() ? "' with reoutingKey '" + groupName + "'" : "") + " ->  " + json);
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
