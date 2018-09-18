package com.github.flaviodev.employee.messagebus.base.pubsub;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.flaviodev.employee.messagebus.base.TopicManagement;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

@Configuration
public class TopicManagementPubsub implements TopicManagement {

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Override
	public void createTopic(String topicName) {
		pubSubAdmin.createTopic(topicName);
	}

	@Override
	public void deleteTopic(String topicName) {
		pubSubAdmin.deleteTopic(topicName);
	}

	@Override
	public List<String> listTopics() {
		return pubSubAdmin.listTopics().stream().map(Topic::getName).collect(Collectors.toList());
	}

	@Override
	public void createSubscription(String subscriptionName, String topicName) {
		pubSubAdmin.createSubscription(subscriptionName, topicName);
	}

	@Override
	public void deleteSubscription(String subscriptionName) {
		pubSubAdmin.deleteSubscription(subscriptionName);
	}

	@Override
	public List<String> listSubscriptions() {
		return pubSubAdmin.listSubscriptions().stream().map(Subscription::getName).collect(Collectors.toList());
	}

	@Bean
	@Primary
	@Override
	public TopicManagement topicManagement() {
		return this;
	}
}
