package com.github.flaviodev.employee.msgbus.base.pubsub;

import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;

import com.github.flaviodev.employee.msgbus.base.MessageSender;

public interface MessageSenderPubsub<T> extends MessageSender<T> {
	
	default PubSubMessageHandler pubSubMessageHandler(PubSubOperations pubsubTemplate) {
		return new PubSubMessageHandler(pubsubTemplate, getTopicName());
	}
}
