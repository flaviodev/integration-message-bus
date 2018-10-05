package com.github.flaviodev.imb.messagebus;

import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageTopic;
import com.github.flaviodev.imb.messagebus.base.PublisherConfig;
import com.github.flaviodev.imb.model.Employee;
import com.google.common.collect.ImmutableMap;

public class PublishEmployeeRouting implements PublisherConfig<PublisherEmployeeRouting> {

	private MessageBusAdmin messageBusAdmin;

	public PublishEmployeeRouting(MessageBusAdmin messageBusAdmin) {
		this.messageBusAdmin = messageBusAdmin;
	}
	
	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Override
	public String getTopicName() {
		return MessageTopic.UPDATE_EMPLOYEE.getName();
	}

	@Override
	public PublisherEmployeeRouting publishMessage() {
		return (employee, groupName) -> getMessageBusAdmin().publishMessage(getTopicName(), groupName, Employee.class,
				employee,ImmutableMap.of("routingKey", groupName));
	}
}