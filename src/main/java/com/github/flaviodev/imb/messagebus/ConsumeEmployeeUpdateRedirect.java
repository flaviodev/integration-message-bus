package com.github.flaviodev.imb.messagebus;

import static org.awaitility.Awaitility.await;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.flaviodev.imb.messagebus.base.ActionOnConsumeMessage;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageSubscription;
import com.github.flaviodev.imb.messagebus.base.RedirectConsumerConfig;
import com.github.flaviodev.imb.model.Employee;
import com.google.common.collect.ImmutableMap;

import lombok.extern.log4j.Log4j;

@Log4j
@Transactional
public class ConsumeEmployeeUpdateRedirect implements RedirectConsumerConfig<Employee> {

	
	private MessageBusAdmin messageBusAdmin;

	public ConsumeEmployeeUpdateRedirect(MessageBusAdmin messageBusAdmin) {
		this.messageBusAdmin = messageBusAdmin;
		log.info("Loading employee update receiver");
		consumeMessage();
	}

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
	public Class<Employee> getPayloadType() {
		return Employee.class;
	}

	@Override
	public String getRedirectGroupName(ImmutableMap<String, String> headers) {
		return headers != null && headers.get("routingKey") != null ? headers.get("routingKey") : "";
	}

	@Override
	public boolean doesMeetTheRedirectionCondition(ImmutableMap<String, String> headers) {
		return headers != null && headers.get("routingKey") != null;
	}

	@Override
	public ActionOnConsumeMessage<Employee> getActionOnConsumeMessage() {
		return (headers, employee) -> {
			if (doesMeetTheRedirectionCondition(headers)) {
				String routingKey = getRedirectGroupName(headers);
				log.info("Redirect for: " + routingKey);

				createSubscriptionForTopicBasedOnRoutingKey(getSubscriptionName(), getTopicName(), routingKey);

				log.info("Processing and routing employee to routingKey '" + routingKey + "' -> " + employee);
				getMessageBusAdmin().publishMessage(getTopicName(), routingKey, Employee.class, employee, null);
			}
		};
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	private void createSubscriptionForTopicBasedOnRoutingKey(String subscriptionName, String topicName,
			String routingKey) {
		getMessageBusAdmin().createSubscriptionForTopic(subscriptionName, topicName, routingKey);

		await().until(() -> getMessageBusAdmin().isRegistredSubscription(subscriptionName, routingKey));
	}

}
