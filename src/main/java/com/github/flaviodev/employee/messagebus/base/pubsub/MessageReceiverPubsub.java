package com.github.flaviodev.employee.messagebus.base.pubsub;

import java.io.IOException;

import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flaviodev.employee.messagebus.base.MessageReceiver;
import com.google.cloud.pubsub.v1.AckReplyConsumer;

public interface MessageReceiverPubsub<T> extends MessageReceiver<T> {

	@Bean
	default MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}

	default PubSubInboundChannelAdapter getMessageChannelAdapter(MessageChannel inputChannel,
			PubSubOperations pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, getSubscritionName());
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);

		return adapter;
	}

	@Override
	default void receive(Class<T> objectClass, Message<?> message) {
		ObjectMapper mapper = new Jackson2ObjectMapperBuilder().build();

		try {
			getMessageProcessor().processPlayload(mapper.readValue(((String)message.getPayload()).getBytes("UTF-8"), objectClass));
		} catch (IOException e) {
			throw new IllegalStateException("Error on desserialize object");
		}

		AckReplyConsumer consumer = (AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
		consumer.ack();
	}

}
