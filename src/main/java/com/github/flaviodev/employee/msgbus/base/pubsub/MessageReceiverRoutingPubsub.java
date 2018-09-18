package com.github.flaviodev.employee.msgbus.base.pubsub;

import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

import com.github.flaviodev.employee.msgbus.base.MessageReceiverRouting;

public interface MessageReceiverRoutingPubsub extends MessageReceiverRouting {

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

}
