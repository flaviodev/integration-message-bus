package com.github.flaviodev.imb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.flaviodev.imb.messagebus.ConsumeEmployeeUpdateRedirect;
import com.github.flaviodev.imb.messagebus.ConsumeEmployeeUpdateTenant1;
import com.github.flaviodev.imb.messagebus.ConsumeEmployeeUpdateTenant2;
import com.github.flaviodev.imb.messagebus.PublishEmployee;
import com.github.flaviodev.imb.messagebus.PublishEmployeeRouting;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;

import lombok.extern.log4j.Log4j;

@Log4j
@Configuration
public class MessageBusConfig {

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Bean
	public ConsumeEmployeeUpdateRedirect consumeEmployeeUpdateRedirect() {
		return new ConsumeEmployeeUpdateRedirect(messageBusAdmin);
	}

	@Bean
	public ConsumeEmployeeUpdateTenant1 consumeEmployeeUpdateTenant1() {
		return new ConsumeEmployeeUpdateTenant1(messageBusAdmin);
	}

	@Bean
	public ConsumeEmployeeUpdateTenant2 consumeEmployeeUpdateTenant2() {
		return new ConsumeEmployeeUpdateTenant2(messageBusAdmin);
	}

	@Bean
	public PublishEmployee publishEmployee() {
		return new PublishEmployee(messageBusAdmin);
	}

	@Bean
	public PublishEmployeeRouting publishEmployeeRoutingConfig() {
		return new PublishEmployeeRouting(messageBusAdmin);
	}
}
