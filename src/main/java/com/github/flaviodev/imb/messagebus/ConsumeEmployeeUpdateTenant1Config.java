package com.github.flaviodev.imb.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.imb.messagebus.base.ConsumerConfig;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageSubscription;
import com.github.flaviodev.imb.model.Employee;
import com.github.flaviodev.imb.tenant.TenantContext;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class ConsumeEmployeeUpdateTenant1Config implements ConsumerConfig {

	private static final String TENANT_ID = "26587a2c89be46b895d6d0f14d182d1a";

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Override
	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName();
	}

	@Override
	public String getGroupName() {
		return TENANT_ID;
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
	public ConsumerConfig consumeMessage() {
		log.info("Loading employee receiver 1");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), getGroupName(), Employee.class,
				(headers, employee) -> {
					TenantContext.setCurrentTenant(TENANT_ID);
					employee.save();
					log.info("Processing and employee to tenant " + TENANT_ID + " :" + employee);
				});
		return this;
	}
}
