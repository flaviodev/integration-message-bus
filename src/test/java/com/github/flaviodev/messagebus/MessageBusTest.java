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
import com.github.flaviodev.employee.messagebus.EmployeeUpdateReceiverRedirect;
import com.github.flaviodev.employee.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.employee.model.Employee;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IntegrationMessageBusApplication.class)
public class MessageBusTest {

	private static final String TENANT_ID = "1251856f568g1688g651g8gg";
	private static final String TOPIC = "employee-test";
	private static final String SUBSCRIPTION = "subscription-employee-test";
	private static final String TOPIC_REDIRECT = TOPIC + "-" + TENANT_ID;
	private static final String SUBSCRIPTION_REDIRECT = SUBSCRIPTION + "-" + TENANT_ID;

	@MockBean
	private EmployeeUpdateReceiverRedirect employeeUpdateReceiverRedirect;

	@Autowired
	MessageBusAdmin messageBusAdmin;

	@Test
	public void shouldSendAndReceiveMessage() {

		mockingEmployeeUpdateReceiverRedirectMethods();

		creatingSubscriptions();

		simulatePublicationOnTopic();

		consumeAndRedirectMessage();

		final Employee employeeReturned = new Employee();
		consumingRedirectedMessage(employeeReturned);

		deletingTopicsAndSubscriptionsCreatedsForTest();

		assertEquals("Flavio", employeeReturned.getName());
	}

	private void consumingRedirectedMessage(final Employee employeeReturned) {
		messageBusAdmin.consumeMessages(SUBSCRIPTION_REDIRECT, TOPIC_REDIRECT, Employee.class, (headers, employee) -> {
			employeeReturned.setName(employee.getName());
		});

		await().timeout(60, TimeUnit.SECONDS).until(() -> employeeReturned.getName() != null);
	}

	private void consumeAndRedirectMessage() {
		employeeUpdateReceiverRedirect.consumeMessage();
	}

	private void simulatePublicationOnTopic() {
		messageBusAdmin.sendMessage(TOPIC, Employee.class, new Employee("Flavio"),
				ImmutableMap.of("tenantId", TENANT_ID));
	}

	private void creatingSubscriptions() {

		messageBusAdmin.createSubscription(SUBSCRIPTION, TOPIC);
		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION));

		messageBusAdmin.createSubscription(SUBSCRIPTION_REDIRECT, TOPIC_REDIRECT);
		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION_REDIRECT));
	}

	private void mockingEmployeeUpdateReceiverRedirectMethods() {
		given(employeeUpdateReceiverRedirect.getMessageBusAdmin()).willReturn(messageBusAdmin);
		given(employeeUpdateReceiverRedirect.getTopicName()).willReturn(TOPIC);
		given(employeeUpdateReceiverRedirect.getSubscriptionName()).willReturn(SUBSCRIPTION);
		given(employeeUpdateReceiverRedirect.getTopicRedirectName(TENANT_ID)).willReturn(TOPIC_REDIRECT);
		given(employeeUpdateReceiverRedirect.consumeMessage()).willCallRealMethod();
	}

	private void deletingTopicsAndSubscriptionsCreatedsForTest() {
		messageBusAdmin.deleteSubscription(SUBSCRIPTION);
		messageBusAdmin.deleteTopic(TOPIC);
		messageBusAdmin.deleteSubscription(SUBSCRIPTION_REDIRECT);
		messageBusAdmin.deleteTopic(TOPIC_REDIRECT);
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

		messageBusAdmin.createSubscription("employee-receive2", "employee2");

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
