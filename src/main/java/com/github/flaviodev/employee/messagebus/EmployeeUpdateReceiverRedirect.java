package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageSubscription;
import com.github.flaviodev.employee.messagebus.base.Receiver;
import com.github.flaviodev.employee.messagebus.base.ReceiverRedirect;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class EmployeeUpdateReceiverRedirect implements ReceiverRedirect {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	public String getSubscriptionName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName();
	}

	public String getTopicName() {
		return MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName();
	}

	public String getTopicRedirectName(String tenantId) {
		return getTopicName() + "-" + tenantId;
	}

	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Bean("employeeUpdateRedirect")
	@Override
	public Receiver consumeMessage() {
		log.info("Loading employee update receiver");
		getMessageBusAdmin().consumeMessages(getSubscriptionName(), getTopicName(), Employee.class,
				(headers, employee) -> {
					String tenantId = headers.get("tenantId");

					if (tenantId != null) {
						log.info("Processing and routing employee to tenant [" + tenantId + "]:" + employee);
						getMessageBusAdmin().sendMessage(getTopicRedirectName(tenantId), Employee.class, employee,
								ImmutableMap.of());
					}
				});
		return this;
	}
}
