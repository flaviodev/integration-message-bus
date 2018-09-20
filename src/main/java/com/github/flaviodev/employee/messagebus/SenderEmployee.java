package com.github.flaviodev.employee.messagebus;

import com.github.flaviodev.employee.model.Employee;

public interface SenderEmployee {
	void send(Employee employee);
}