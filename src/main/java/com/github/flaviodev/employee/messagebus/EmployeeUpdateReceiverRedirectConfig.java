package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageSubscription;
import com.github.flaviodev.employee.messagebus.base.ReceiverConfig;
import com.github.flaviodev.employee.messagebus.base.ReceiverRedirectConfig;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class EmployeeUpdateReceiverRedirectConfig implements ReceiverRedirectConfig {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName();
	}

	public String getTopicName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName();
	}

	@SneakyThrows
	public String getTopicRedirectName(String routingKey) {
		throw new IllegalAccessException(
				"The RabbitMQ implementation does not use a different topic (exchange) for message routing");
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Bean("employeeUpdateRedirect")
	@Override
	public ReceiverConfig consumeMessage() {
		log.info("Loading employee update receiver");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), Employee.class,
				(headers, employee) -> {
					String routingKey = headers.get("routingKey");

					if (routingKey != null) {
						createSubscriptionForTopicBasedOnRoutingKey(getSubscriptionName(), getTopicName(), routingKey);

						log.info("Processing and routing employee to routingKey '" + routingKey + "' -> "
								+ employee);
						getMessageBusAdmin().sendMessage(getTopicName(), Employee.class, employee,
								ImmutableMap.of("routingKey", routingKey));
					}
				});
		return this;
	}

	private void createSubscriptionForTopicBasedOnRoutingKey(String subscriptionName, String topicName,
			String routingKey) {
		getMessageBusAdmin().createSubscriptionForTopic(subscriptionName + "-" + routingKey, topicName + "-" + routingKey);
	}
}
