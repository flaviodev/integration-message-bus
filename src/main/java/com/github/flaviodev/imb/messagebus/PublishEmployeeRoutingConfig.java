package com.github.flaviodev.imb.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageTopic;
import com.github.flaviodev.imb.messagebus.base.PublisherConfig;
import com.github.flaviodev.imb.model.Employee;
import com.google.common.collect.ImmutableMap;

@Configuration
public class PublishEmployeeRoutingConfig implements PublisherConfig<PublisherEmployeeRouting> {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Override
	public String getTopicName() {
		return MessageTopic.UPDATE_EMPLOYEE.getName();
	}

	@Bean("publisherEmployeeRouting")
	@Override
	public PublisherEmployeeRouting publishMessage() {
		return (employee, groupName) -> getMessageBusAdmin().publishMessage(getTopicName(), groupName, Employee.class,
				employee,ImmutableMap.of("routingKey", groupName));
	}
}