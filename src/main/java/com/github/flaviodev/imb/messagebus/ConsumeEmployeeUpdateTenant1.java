package com.github.flaviodev.imb.messagebus;

import com.github.flaviodev.imb.messagebus.base.ActionOnConsumeMessage;
import com.github.flaviodev.imb.messagebus.base.ConsumerConfig;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageSubscription;
import com.github.flaviodev.imb.model.Employee;
import com.github.flaviodev.imb.tenant.TenantContext;

import lombok.extern.log4j.Log4j;

@Log4j
public class ConsumeEmployeeUpdateTenant1 implements ConsumerConfig<Employee> {

	private static final String TENANT_ID = "26587a2c89be46b895d6d0f14d182d1a";

	private MessageBusAdmin messageBusAdmin;

	public ConsumeEmployeeUpdateTenant1(MessageBusAdmin messageBusAdmin) {
		this.messageBusAdmin = messageBusAdmin;
		log.info("Loading employee receiver 1");
		consumeMessage();
	}
	
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
	public Class<Employee> getPayloadType() {
		return Employee.class;
	}

	@Override
	public ActionOnConsumeMessage<Employee> getActionOnConsumeMessage() {
		return (headers, employee) -> {
			TenantContext.setCurrentTenant(TENANT_ID);
			employee.save();
			log.info("Processing and employee to tenant1 " + TENANT_ID + " :" + employee);
		};
	}
	
	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}


}
