package com.github.flaviodev.employee.messagebus;

import com.github.flaviodev.employee.model.Employee;

public interface SenderEmployeeRouting {
	void send(String routingKey, Employee employee);
}