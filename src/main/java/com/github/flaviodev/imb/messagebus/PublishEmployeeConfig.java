package com.github.flaviodev.imb.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageTopic;
import com.github.flaviodev.imb.messagebus.base.PublisherConfig;
import com.github.flaviodev.imb.model.Employee;
import com.google.common.collect.ImmutableMap;

@Configuration
@Transactional
public class PublishEmployeeConfig implements PublisherConfig<PublisherEmployee> {

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
		

	@Bean("publisherEmployee")
	@Override
	public PublisherEmployee publishMessage() {
		return employee -> getMessageBusAdmin().publishMessage(getTopicName(), null, Employee.class, employee, null);
	}
}