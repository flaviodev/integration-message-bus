package com.github.flaviodev.employee.messagebus.base.pubsub;

public interface PubSubMessagingGateway {
	void sendToPubsub(Object playload);
}