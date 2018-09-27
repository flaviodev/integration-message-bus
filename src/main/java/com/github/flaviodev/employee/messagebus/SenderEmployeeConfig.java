package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageTopic;
import com.github.flaviodev.employee.messagebus.base.SenderConfig;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

@Configuration
public class SenderEmployeeConfig implements SenderConfig<SenderEmployee> {

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

	@Bean("senderEmployee")
	@Override
	public SenderEmployee sendMessage() {
		return employee -> getMessageBusAdmin().sendMessage(getTopicName(), Employee.class, employee,
				ImmutableMap.of());
	}
}