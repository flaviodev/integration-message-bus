package com.github.flaviodev.employee.msgbus.base.pubsub;

public interface PubSubMessagingGateway {
	void sendToPubsub(Object playload);
}