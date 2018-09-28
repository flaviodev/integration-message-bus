package com.github.flaviodev.imb.messagebus;

import static org.awaitility.Awaitility.await;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.imb.messagebus.base.ConsumerConfig;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageSubscription;
import com.github.flaviodev.imb.model.Employee;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class ConsumeEmployeeUpdateRedirectConfig implements ConsumerConfig {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName();
	}

	@Override
	public String getGroupName() {
		return "";
	}
	
	public String getTopicName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName();
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Bean("employeeUpdateRedirect")
	@Override
	public ConsumerConfig consumeMessage() {
		log.info("Loading employee update receiver");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), getGroupName(), Employee.class,
				(headers, employee) -> {
					String routingKey = headers.get("routingKey");

					if (routingKey != null) {
						createSubscriptionForTopicBasedOnRoutingKey(getSubscriptionName(), getTopicName(), routingKey);

						log.info("Processing and routing employee to routingKey '" + routingKey + "' -> "
								+ employee);
						getMessageBusAdmin().publishMessage(getTopicName(), routingKey, Employee.class, employee, null);
					}
				});
		return this;
	}

	private void createSubscriptionForTopicBasedOnRoutingKey(String subscriptionName, String topicName,
			String routingKey) {
		getMessageBusAdmin().createSubscriptionForTopic(subscriptionName, topicName, routingKey);
		
		await().until(() -> messageBusAdmin.isRegistredSubscription(subscriptionName, routingKey));
	}
}
