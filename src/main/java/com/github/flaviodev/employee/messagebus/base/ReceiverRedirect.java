package com.github.flaviodev.employee.messagebus.base;

public interface ReceiverRedirect extends Receiver {

	String getTopicRedirectName(String tenantId);
}
