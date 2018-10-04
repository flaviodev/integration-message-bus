package com.github.flaviodev.imb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.flaviodev.imb.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
	
	Employee findByName(String name);

}
