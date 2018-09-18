package com.github.flaviodev.employee.messagebus.base;

public interface MessageProcessor<T> {
	void processPlayload(T payload);
}
