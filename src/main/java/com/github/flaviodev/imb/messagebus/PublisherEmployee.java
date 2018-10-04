package com.github.flaviodev.imb.messagebus;

import com.github.flaviodev.imb.model.Employee;

public interface PublisherEmployee {
	void publishEmployee(Employee employee);
}