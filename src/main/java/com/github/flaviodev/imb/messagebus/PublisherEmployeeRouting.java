package com.github.flaviodev.imb.messagebus;

import com.github.flaviodev.imb.model.Employee;

public interface PublisherEmployeeRouting {
	void publishEmployeeToGroup(Employee employee, String groupName);
}