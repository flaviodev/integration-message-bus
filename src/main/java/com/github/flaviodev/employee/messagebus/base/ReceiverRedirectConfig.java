package com.github.flaviodev.employee.messagebus.base;

public interface ReceiverRedirectConfig extends ReceiverConfig {

	String getTopicRedirectName(String tenantId);
}
