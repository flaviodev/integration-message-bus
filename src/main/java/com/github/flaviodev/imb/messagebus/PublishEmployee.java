package com.github.flaviodev.imb.messagebus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.github.flaviodev.imb.messagebus.PublisherEmployee;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.messagebus.base.MessageTopic;
import com.github.flaviodev.imb.messagebus.base.PublisherConfig;
import com.github.flaviodev.imb.model.Employee;

@Transactional
public class PublishEmployee implements PublisherConfig<PublisherEmployee> {

	private MessageBusAdmin messageBusAdmin;

	public PublishEmployee(MessageBusAdmin messageBusAdmin) {
		this.messageBusAdmin = messageBusAdmin;
	}
	
	@Override
	public MessageBusAdmin getMessageBusAdmin() {
		return messageBusAdmin;
	}

	@Override
	public String getTopicName() {
		return MessageTopic.UPDATE_EMPLOYEE.getName();
	}
		
	@Override
	public PublisherEmployee publishMessage() {
		return employee -> getMessageBusAdmin().publishMessage(getTopicName(), null, Employee.class, employee, null);
	}
}