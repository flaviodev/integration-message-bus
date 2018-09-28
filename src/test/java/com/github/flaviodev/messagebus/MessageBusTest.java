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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.flaviodev.imb.IntegrationMessageBusApplication;
import com.github.flaviodev.imb.SpringContext;
import com.github.flaviodev.imb.messagebus.ConsumeEmployeeUpdateRedirectConfig;
import com.github.flaviodev.imb.messagebus.ConsumeEmployeeUpdateTenant1Config;
import com.github.flaviodev.imb.messagebus.base.MessageBusAdmin;
import com.github.flaviodev.imb.model.Employee;
import com.github.flaviodev.imb.persistence.UUIDGenerator;
import com.github.flaviodev.imb.repository.EmployeeRepository;
import com.github.flaviodev.imb.tenant.TenantContext;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IntegrationMessageBusApplication.class)
public class MessageBusTest {

	private static final String TOPIC_TEST = "employee-test";
	private static final String SUBSCRIPTION_TEST = "subscription-employee-test";

	private static final String GROUP_TENANT1_TEST = "26587a2c89be46b895d6d0f14d182d1a";

	private static final String NAME_EMPLOYEE_TEST = "Employee_" + UUIDGenerator.uuid();

	@MockBean
	private ConsumeEmployeeUpdateRedirectConfig consumeEmployeeUpdateRedirect;

	@MockBean
	private ConsumeEmployeeUpdateTenant1Config consumeEmployeeUpdateTenant1;

	@Autowired
	private MessageBusAdmin messageBusAdmin;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	public void shouldSendAndReceiveMessage() {

		mockingConsumeEmployeeUpdateRedirectMethods();
		mockingConsumeEmployeeUpdateTenant1Methods();

		creatingSubscriptions();

		simulatePublicationOnTopic();

		consumeEmployeeUpdateRedirect.consumeMessage();

		await().pollDelay(10, TimeUnit.SECONDS);
		
		consumeEmployeeUpdateTenant1.consumeMessage();

		waitingConsumeEmployeeUpdateTenant1();

		deletingTopicsAndSubscriptionsCreatedsForTest();

		assertEquals(NAME_EMPLOYEE_TEST, employeeRepository.findByName(NAME_EMPLOYEE_TEST).getName());
	}

	private void mockingConsumeEmployeeUpdateRedirectMethods() {
		given(consumeEmployeeUpdateRedirect.getMessageBusAdmin()).willReturn(messageBusAdmin);
		given(consumeEmployeeUpdateRedirect.getTopicName()).willReturn(TOPIC_TEST);
		given(consumeEmployeeUpdateRedirect.getSubscriptionName()).willReturn(SUBSCRIPTION_TEST);
		given(consumeEmployeeUpdateRedirect.getGroupName()).willReturn(null);
		given(consumeEmployeeUpdateRedirect.consumeMessage()).willCallRealMethod();
	}

	private void mockingConsumeEmployeeUpdateTenant1Methods() {
		given(consumeEmployeeUpdateTenant1.getMessageBusAdmin()).willReturn(messageBusAdmin);
		given(consumeEmployeeUpdateTenant1.getTopicName()).willReturn(TOPIC_TEST);
		given(consumeEmployeeUpdateTenant1.getSubscriptionName()).willReturn(SUBSCRIPTION_TEST);
		given(consumeEmployeeUpdateTenant1.getGroupName()).willReturn(GROUP_TENANT1_TEST);
		given(consumeEmployeeUpdateTenant1.consumeMessage()).willCallRealMethod();
	}

	private void creatingSubscriptions() {
		messageBusAdmin.createSubscriptionForTopic(SUBSCRIPTION_TEST, TOPIC_TEST, null);
		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION_TEST, null));

		messageBusAdmin.createSubscriptionForTopic(SUBSCRIPTION_TEST, TOPIC_TEST, GROUP_TENANT1_TEST);
		await().until(() -> messageBusAdmin.isRegistredSubscription(SUBSCRIPTION_TEST, GROUP_TENANT1_TEST));
	}

	private void simulatePublicationOnTopic() {
		messageBusAdmin.publishMessage(TOPIC_TEST, "", Employee.class, new Employee(NAME_EMPLOYEE_TEST),
				ImmutableMap.of("routingKey", GROUP_TENANT1_TEST));
	}

	private void waitingConsumeEmployeeUpdateTenant1() {
		TenantContext.setCurrentTenant(GROUP_TENANT1_TEST);
		await().pollDelay(5, TimeUnit.SECONDS).timeout(60, TimeUnit.SECONDS).ignoreExceptions()
				.until(() -> employeeRepository.findByName(NAME_EMPLOYEE_TEST) != null);
	}

	private void deletingTopicsAndSubscriptionsCreatedsForTest() {
		messageBusAdmin.deleteSubscription(SUBSCRIPTION_TEST, null);
		messageBusAdmin.deleteTopic(TOPIC_TEST, null);

		messageBusAdmin.deleteSubscription(SUBSCRIPTION_TEST, GROUP_TENANT1_TEST);
		messageBusAdmin.deleteTopic(TOPIC_TEST, GROUP_TENANT1_TEST);
	}

	@Test
	public void shouldCreateAndDeleteTopic() {

		messageBusAdmin.createTopic("employee", null);

		await().until(() -> messageBusAdmin.isRegistredTopic("employee", null));

		assertTrue("should return the created topic", messageBusAdmin.isRegistredTopic("employee", null));

		messageBusAdmin.deleteTopic("employee", null);

		await().until(() -> !messageBusAdmin.isRegistredTopic("employee", null));

		assertFalse("should not return the created topic", messageBusAdmin.isRegistredTopic("employee", null));
	}

	@Test
	public void shouldCreateAndDeleteSubscription() {

		messageBusAdmin.createSubscriptionForTopic("employee-receive2", "employee2", null);

		await().until(() -> messageBusAdmin.isRegistredSubscription("employee-receive2", null));

		assertTrue("should return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2", null));

		messageBusAdmin.deleteSubscription("employee-receive2", null);

		await().until(() -> !messageBusAdmin.isRegistredSubscription("employee-receive2", null));

		assertFalse("should not return the created subscription",
				messageBusAdmin.isRegistredSubscription("employee-receive2", null));

		messageBusAdmin.deleteTopic("employee2", null);
	}

}
