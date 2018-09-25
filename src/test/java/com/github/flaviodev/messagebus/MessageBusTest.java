package com.github.flaviodev.messagebus;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.flaviodev.employee.IntegrationMessageBusApplication;
import com.github.flaviodev.employee.SpringContext;
import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IntegrationMessageBusApplication.class)
public class MessageBusTest {

	@Test
	public void shouldCreateAndDeleteTopic() {
		MessageBusAdmin messageBusAdmin = SpringContext.getBean(MessageBusAdmin.class);
		messageBusAdmin.createTopic("employee");

		assertTrue("should return the created topic", messageBusAdmin.isRegistredTopic("employee"));

		messageBusAdmin.deleteTopic("employee");

		assertFalse("should not return the created topic", messageBusAdmin.isRegistredTopic("employee"));

	}

	@Test
	public void shouldCreateAndDeleteSubscription() {
		MessageBusAdmin messageBusAdmin = SpringContext.getBean(MessageBusAdmin.class);

		messageBusAdmin.createTopic("employee2");
		messageBusAdmin.createSubscription("employee-receive2", "employee2", null);

		assertTrue("should return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2"));

		messageBusAdmin.deleteSubscription("employee-receive2");

		assertFalse("should not return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2"));

		messageBusAdmin.deleteTopic("employee2");
	}

	@Test
	public void shouldSendAndReceiveMessage() {
		MessageBusAdmin messageBusAdmin = SpringContext.getBean(MessageBusAdmin.class);
		messageBusAdmin.createTopic("employee3");
		messageBusAdmin.createSubscription("employee-receive3", "employee3", null);

		final Employee employeeReturned = new Employee();

		messageBusAdmin.sendMessage("employee3", Employee.class, new Employee("Flavio"), ImmutableMap.of());

		messageBusAdmin.consumeMessages("employee-receive3", "employee3", null, Employee.class, (headers, employee) -> {
			employeeReturned.setName(employee.getName());
		});

		await().until(() -> employeeReturned.getName() != null);

		messageBusAdmin.deleteSubscription("employee-receive3");
		messageBusAdmin.deleteTopic("employee3");

		assertEquals("Flavio", employeeReturned.getName());
	}

}
