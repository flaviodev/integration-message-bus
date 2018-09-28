package com.github.flaviodev.messagebus;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.flaviodev.employee.IntegrationMessageBusApplication;
import com.github.flaviodev.employee.SpringContext;
import com.github.flaviodev.employee.messagebus.EmployeeUpdateReceiverRedirectConfig;
import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IntegrationMessageBusApplication.class)
public class MessageBusTest {

	private static final String TENANT_ID = "1251856f568g1688g651g8gg";
	private static final String TOPIC = "employee-test";
	private static final String SUBSCRIPTION = "subscription-employee-test" + "-" + TENANT_ID;

	@MockBean
	private EmployeeUpdateReceiverRedirectConfig employeeUpdateReceiverRedirect;

	@Autowired
	MessageBusAdmin messageBusAdmin;

	@Test
	public void shouldSendAndReceiveMessage() {

		mockingEmployeeUpdateReceiverRedirectMethods();

		creatingSubscriptions();

		simulatePublicationOnTopic();

	//	consumeAndRedirectMessage();

		final Employee employeeReturned = new Employee();
		consumingRedirectedMessage(employeeReturned);

		deletingTopicsAndSubscriptionsCreatedsForTest();

		assertEquals("Flavio", employeeReturned.getName());
	}

	private void consumingRedirectedMessage(final Employee employeeReturned) {
		messageBusAdmin.consumeMessages(SUBSCRIPTION, TOPIC, Employee.class, (headers, employee) -> {
			employeeReturned.setName(employee.getName());
		});

		await().timeout(60, TimeUnit.SECONDS).until(() -> employeeReturned.getName() != null);
	}


	private void simulatePublicationOnTopic() {
		messageBusAdmin.sendMessage(TOPIC, Employee.class, new Employee("Flavio"),
				ImmutableMap.of("routingKey", TENANT_ID));
	}

	private void creatingSubscriptions() {

		messageBusAdmin.createSubscriptionForTopic(SUBSCRIPTION, TOPIC);
		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION));

//		messageBusAdmin.createSubscriptionForTopic(SUBSCRIPTION_REDIRECT, TOPIC);
//		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION_REDIRECT));
	}

	private void mockingEmployeeUpdateReceiverRedirectMethods() {
		given(employeeUpdateReceiverRedirect.getMessageBusAdmin()).willReturn(messageBusAdmin);
		given(employeeUpdateReceiverRedirect.getTopicName()).willReturn(TOPIC);
		given(employeeUpdateReceiverRedirect.getSubscriptionName()).willReturn(SUBSCRIPTION);
		given(employeeUpdateReceiverRedirect.consumeMessage()).willCallRealMethod();
	}

	private void deletingTopicsAndSubscriptionsCreatedsForTest() {
		messageBusAdmin.deleteSubscription(SUBSCRIPTION);
		messageBusAdmin.deleteTopic(TOPIC);
//		messageBusAdmin.deleteSubscription(SUBSCRIPTION_REDIRECT);
	}

	@Test
	public void shouldCreateAndDeleteTopic() {
		MessageBusAdmin messageBusAdmin = SpringContext.getBean(MessageBusAdmin.class);

		messageBusAdmin.createTopic("employee");

		await().until(() -> messageBusAdmin.isRegistredTopic("employee"));

		assertTrue("should return the created topic", messageBusAdmin.isRegistredTopic("employee"));

		messageBusAdmin.deleteTopic("employee");

		await().until(() -> !messageBusAdmin.isRegistredTopic("employee"));

		assertFalse("should not return the created topic", messageBusAdmin.isRegistredTopic("employee"));
	}

	@Test
	public void shouldCreateAndDeleteSubscription() {
		MessageBusAdmin messageBusAdmin = SpringContext.getBean(MessageBusAdmin.class);

		messageBusAdmin.createSubscriptionForTopic("employee-receive2", "employee2");

		await().until(() -> messageBusAdmin.isRegistredSubscription("employee-receive2"));

		assertTrue("should return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2"));

		messageBusAdmin.deleteSubscription("employee-receive2");

		await().until(() -> !messageBusAdmin.isRegistredSubscription("employee-receive2"));

		assertFalse("should not return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2"));

		messageBusAdmin.deleteTopic("employee2");
	}

}
