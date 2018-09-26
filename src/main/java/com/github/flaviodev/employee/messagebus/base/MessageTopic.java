package com.github.flaviodev.employee.messagebus.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MessageTopic {

	UPDATE_EMPLOYEE("topic-update-employee");

	private @Getter String name;
}
