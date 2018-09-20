package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageTopic;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

@Configuration
public class SenderEmployeeConfig {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Bean
	public SenderEmployee getSenderSenderEmployee() {
		return employee -> messageBusAdmin.sendMessage(MessageTopic.UPDATE_EMPLOYEE, Employee.class, employee, ImmutableMap.of());
	}

	@Bean
	public SenderEmployeeRouting getSenderSenderEmployeeRouting() {
		return (tenantId, employee) -> messageBusAdmin.sendMessage(MessageTopic.UPDATE_EMPLOYEE, Employee.class,
				employee, ImmutableMap.of("tenantId", tenantId));
	}
}