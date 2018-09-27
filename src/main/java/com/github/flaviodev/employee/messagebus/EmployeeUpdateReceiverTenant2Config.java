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
public class EmployeeUpdateReceiverTenant2Config implements ReceiverConfig {

	private static final String TENANT_ID = "dcab14bd67a542b68068d995a96adbdf";

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Override
	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName() + "-" + TENANT_ID;
	}

	@Override
	public String getTopicName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName() + "-" + TENANT_ID;
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Bean("employeeUpdateTenant2")
	@Override
	public ReceiverConfig consumeMessage() {
		log.info("Loading employee receiver 2");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), Employee.class,
				(headers, employee) -> log.info("Processing and employee to tenant 2 :" + employee));
		return this;
	}
}
