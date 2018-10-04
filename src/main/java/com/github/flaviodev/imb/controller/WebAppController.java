package com.github.flaviodev.imb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.github.flaviodev.imb.SpringContext;
import com.github.flaviodev.imb.messagebus.PublisherEmployeeRouting;
import com.github.flaviodev.imb.model.Employee;

@RestController
public class WebAppController {

	@PostMapping("/publishMessage")
	public RedirectView publishMessage(@RequestParam("tenantId") String tenantId,
			@RequestParam("employeeName") String employeeName) {

		Employee employee = new Employee(employeeName);

		SpringContext.getBean(PublisherEmployeeRouting.class).publishEmployeeToGroup(employee, tenantId);
		return new RedirectView("/");
	}
}
