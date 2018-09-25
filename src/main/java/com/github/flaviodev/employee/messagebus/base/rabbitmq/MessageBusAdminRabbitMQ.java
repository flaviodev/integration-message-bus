package com.github.flaviodev.employee.messagebus.base.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitManagementTemplate;
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

	@Override
	@SneakyThrows
	public void createTopic(@NonNull String topicName) {
		getChannel().exchangeDeclare(topicName, "direct");
	}

	@Override
	@SneakyThrows
	public void deleteTopic(@NonNull String topicName) {
		getChannel().exchangeDelete(topicName);
	}

	@Override
	public List<String> listTopics() {
		return getRabbitManagementTemplate().getExchanges().stream().map(Exchange::getName)
				.collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public void createSubscription(@NonNull String subscriptionName, @NonNull String topicName, String tenantId) {
		getChannel().queueDeclare(subscriptionName, true, false, false, null);
		getChannel().queueBind(subscriptionName, topicName, getRoutingKey(topicName, tenantId));
	}

	private String getRoutingKey(String topicName, String tenantId) {
		return tenantId != null ? topicName + "." + tenantId : topicName;
	}

	@Override
	@SneakyThrows
	public void deleteSubscription(@NonNull String subscriptionName) {
		getChannel().queueDelete(subscriptionName);
	}

	@Override
	public List<String> listSubscriptions() {
		return getRabbitManagementTemplate().getQueues().stream().map(Queue::getName).collect(Collectors.toList());
	}

	@Bean
	@Primary
	@Override
	public MessageBusAdmin messageBusAdmin() {
		return this;
	}

	@Override
	public boolean isRegistredTopic(@NonNull String topicName) {
		return listTopics().stream().filter(topic -> topic.equals(topicName)).count() > 0;
	}

	@Override
	public boolean isRegistredSubscription(@NonNull String subscriptionName) {
		return listSubscriptions().stream().filter(subscription -> subscription.equals(subscriptionName)).count() > 0;
	}

	@Override
	public void verifySubscription(@NonNull String subscriptionName, @NonNull String topicName, String tentantId) {

		if (!isRegistredTopic(topicName))
			createTopic(topicName);

		if (!isRegistredSubscription(subscriptionName))
			createSubscription(subscriptionName, topicName, tentantId);
	}

	@Override
	public <T> MessageBusAdmin consumeMessages(@NonNull MessageSubscription subscription, String tentantId,
			@NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {
		return consumeMessages(subscription.getName(), subscription.getTopicName(), tentantId, payloadType, action);
	}

	@Override
	@SneakyThrows
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			String tenantId, @NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		verifySubscription(subscriptionName, topicName, tenantId);

		Consumer consumer = new DefaultConsumer(getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				Logger logger = Logger.getLogger(DefaultConsumer.class);

				String message = new String(body, "UTF-8");
				logger.info("consuming message [" + subscriptionName + "]: " + message);
				action.apply(ImmutableMap.of(), parseJson(payloadType, body));
			}
		};
		getChannel().basicConsume(subscriptionName, true, consumer);
		return this;
	}

	@Override
	public <T> void sendMessage(@NonNull MessageTopic topic, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		sendMessage(topic.getName(), payloadType, payloadObject, headers);
	}

	@Override
	@SneakyThrows
	public <T> void sendMessage(@NonNull String topicName, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {
		if (headers == null)
			headers = ImmutableMap.of();

		String tenantId = headers.get("tenantId");

		String json = stringfyJson(payloadObject);

		getChannel().basicPublish(topicName, getRoutingKey(topicName, tenantId), null, json.getBytes());
		log.info("Sending message [" + getRoutingKey(topicName, tenantId) + "]: " + json);
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
