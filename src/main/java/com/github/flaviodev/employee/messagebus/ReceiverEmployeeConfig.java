package com.github.flaviodev.employee.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.messagebus.base.MessageSubscription;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class ReceiverEmployeeConfig {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Bean
	public MessageBusAdmin getMesssagesUpdateEmployee26587a2c89be46b895d6d0f14d182d1a() {
		return getMesssagesUpdateEmployeeRouting("26587a2c89be46b895d6d0f14d182d1a");
	}

	@Bean
	public MessageBusAdmin getMesssagesUpdateEmployeedcab14bd67a542b68068d995a96adbdf() {
		return getMesssagesUpdateEmployeeRouting("dcab14bd67a542b68068d995a96adbdf");
	}

	private MessageBusAdmin getMesssagesUpdateEmployeeRouting(String tenantId) {
		return messageBusAdmin.consumeMessages(MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getName() + "-" + tenantId,
				MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName(), tenantId, Employee.class,
				(headers, employee) -> log.info("Receiving employee on tenant [" + tenantId + "]:" + employee));
	}

	@Bean
	public MessageBusAdmin getMesssagesUpdateEmployee() {
		return messageBusAdmin.consumeMessages(MessageSubscription.UPDATE_EMPLOYEE_DEFAULT, null, Employee.class,
				(headers, employee) -> {
					String tenantId = headers.get("tenantId");

					if (tenantId != null) {
						log.info("Processing and routing employee to tenant [" + tenantId + "]:" + employee);
						messageBusAdmin.sendMessage(
								MessageSubscription.UPDATE_EMPLOYEE_DEFAULT.getTopicName() + "-" + tenantId,
								Employee.class, employee, ImmutableMap.of());
					}
				});
	}
}