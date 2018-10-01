package com.github.flaviodev.imb.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.github.flaviodev.imb.messagebus.base.ConsumerConfig;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageSubscription;
import com.github.flaviodev.imb.model.Employee;
import com.github.flaviodev.imb.tenant.TenantContext;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
@Transactional
public class ConsumeEmployeeUpdateTenant2Config implements ConsumerConfig {

	private static final String TENANT_ID = "dcab14bd67a542b68068d995a96adbdf";

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

	@Bean("employeeUpdateTenant2")
	@Override
	public ConsumerConfig consumeMessage() {
		log.info("Loading employee receiver 2");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), getGroupName(), Employee.class,
				(headers, employee) -> {
					TenantContext.setCurrentTenant(TENANT_ID);
					employee.save();
					log.info("Processing and employee to tenant " + TENANT_ID + " :" + employee);
				});
		return this;
	}
}
