package com.github.flaviodev.imb.messagebus.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MessageSubscription {

	UPDATE_EMPLOYEE_DEFAULT("subscription-update-employee", MessageTopic.UPDATE_EMPLOYEE);

	private @Getter String name;
	private @Getter MessageTopic topic;

	public String getTopicName() {
		return topic != null ? topic.getName() : null;
	}
}
