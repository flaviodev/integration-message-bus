package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageSubscription;
import com.github.flaviodev.employee.messagebus.base.ReceiverConfig;
import com.github.flaviodev.employee.model.Employee;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class EmployeeUpdateReceiverTenant1Config implements ReceiverConfig {

	private static final String TENANT_ID = "26587a2c89be46b895d6d0f14d182d1a";

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Override
	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName() + "-" + TENANT_ID;
	}

	@Override
	public String getTopicName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName();
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Bean("employeeUpdateTenant1")
	@Override
	public ReceiverConfig consumeMessage() {
		log.info("Loading employee receiver 1");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), Employee.class,
				(headers, employee) -> log.info("Processing and employee to tenant 1 :" + employee));
		return this;
	}
}
