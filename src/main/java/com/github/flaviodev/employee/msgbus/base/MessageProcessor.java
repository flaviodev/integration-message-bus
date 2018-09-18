package com.github.flaviodev.employee.msgbus.base;

public interface MessageProcessor<T> {
	void processPlayload(T payload);
}
