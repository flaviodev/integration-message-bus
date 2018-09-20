package com.github.flaviodev.employee.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.flaviodev.employee.SpringContext;
import com.github.flaviodev.employee.messagebus.SenderEmployeeRouting;
import com.github.flaviodev.employee.model.Employee;

@RestController
public class WebAppController {

	@PostMapping("/publishMessage")
	public RedirectView publishMessage(@RequestParam("tenantId") String tenantId,
			@RequestParam("employeeName") String employeeName) throws JsonProcessingException {

		Employee employee = new Employee(employeeName);
		employee.save();

		SpringContext.getBean(SenderEmployeeRouting.class).send(tenantId, employee);
		return new RedirectView("/");
	}
}
