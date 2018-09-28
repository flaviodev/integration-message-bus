package com.github.flaviodev.employee.messagebus.base.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

		if (!isRegistredTopic(topicName)) {
			log.info("Creating topic '" + topicName + "'");
			getChannel().exchangeDeclare(topicName, "direct");
		}
	}

	@Override
	@SneakyThrows
	public void deleteTopic(@NonNull String topicName) {
		if (isRegistredTopic(topicName)) {
			log.info("Deleting topic '" + topicName + "'");
			getChannel().exchangeDelete(topicName);
		}
	}

	@Override
	public List<String> listTopics() {
		return getRabbitManagementTemplate().getExchanges().stream().map(Exchange::getName)
				.collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public void createSubscriptionForTopic(@NonNull String subscriptionName, @NonNull String topicName) {

		if (!isRegistredSubscription(subscriptionName)) {
			log.info("Creating subscription '" + subscriptionName + "' for topic '" + topicName + "'");
			getChannel().queueDeclare(subscriptionName, true, false, false, null);
			getChannel().queueBind(subscriptionName, topicName, topicName);
		}
	}

	@Override
	@SneakyThrows
	public void deleteSubscription(@NonNull String subscriptionName) {
		if (isRegistredSubscription(subscriptionName)) {
			log.info("Deleting subscription '" + subscriptionName + "'");
			getChannel().queueDelete(subscriptionName);
		}
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
	@SneakyThrows
	public <T> MessageBusAdmin consumeMessages(@NonNull String subscriptionName, @NonNull String topicName,
			@NonNull Class<T> payloadType, @NonNull ActionOnConsumeMessage<T> action) {

		createSubscriptionForTopic(subscriptionName, topicName);

		Consumer consumer = new DefaultConsumer(getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				String message = new String(body, "UTF-8");

				log.info("Consuming message of the subscription '" + subscriptionName + "' -> " + message);
				action.apply(ImmutableMap.of(), parseJson(payloadType, body));
			}
		};
		getChannel().basicConsume(subscriptionName, true, consumer);
		return this;
	}

	@Override
	@SneakyThrows
	public <T> void sendMessage(@NonNull String topicName, @NonNull Class<T> payloadType, @NonNull T payloadObject,
			ImmutableMap<String, String> headers) {

		if (headers == null)
			headers = ImmutableMap.of();

		createTopic(topicName);

		String routingKey = headers.get("routingKey");
		
		String redirectionRoutingKey = generateRedirectingRoutingKeyBasedOnRoutingKeyAndTopicName(routingKey, topicName);

		String json = stringfyJson(payloadObject);

		getChannel().basicPublish(topicName, redirectionRoutingKey, null, json.getBytes());

		log.info("Sending message to topic '" + topicName + "' with routingKey '" + redirectionRoutingKey + "' ->  " + json);
	}
	
	private String generateRedirectingRoutingKeyBasedOnRoutingKeyAndTopicName(String routingKey,
			String topicName) {
		return topicName + (routingKey != null ? "-" + routingKey : "");
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
