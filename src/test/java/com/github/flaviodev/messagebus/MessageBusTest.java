package com.github.flaviodev.messagebus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.flaviodev.employee.IntegrationMessageBusApplication;
import com.github.flaviodev.employee.SpringContext;
import com.github.flaviodev.employee.config.sender.MessageSenderUser;
import com.github.flaviodev.employee.model.Employee;
import com.github.flaviodev.employee.msgbus.base.TopicManagement;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IntegrationMessageBusApplication.class)
public class MessageBusTest {

	@Test
	public void shouldSendMessage() throws JsonProcessingException, InterruptedException {
		MessageSenderUser sender = SpringContext.getBean(MessageSenderUser.class);
		sender.send(new Employee("Flavio"));
		Thread.sleep(10_000);
	}
	
	@Test
	public void shouldListTopics() throws JsonProcessingException, InterruptedException {
		TopicManagement admin = SpringContext.getBean(TopicManagement.class);
		admin.listTopics().forEach(System.out::println);
	}
}
