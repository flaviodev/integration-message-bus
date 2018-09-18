package com.github.flaviodev.employee.msgbus.base.pubsub;

import org.springframework.messaging.handler.annotation.Header;

public interface PubSubMessagingGatewayRouting {
	void sendToPubsub(@Header("destinationId") Object destinationId, @Header("operation") String operation,
			@Header("typePayload") Class<?> typePayload, Object playload);
}