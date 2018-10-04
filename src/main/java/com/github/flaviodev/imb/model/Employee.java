package com.github.flaviodev.imb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.github.flaviodev.imb.model.base.EntittyBaseCRUD;
import com.github.flaviodev.imb.persistence.UUIDGenerator;
import com.github.flaviodev.imb.repository.EmployeeRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public @Getter @Setter class Employee extends EntittyBaseCRUD<String, Employee, EmployeeRepository> {

	private static final long serialVersionUID = -7388569779385135795L;

	@Id
	@GeneratedValue(generator = UUIDGenerator.NAME)
	@GenericGenerator(name = UUIDGenerator.NAME, strategy = UUIDGenerator.PACKAGE_PATH)
	@Column(name = "employeeid", length = 32)
	private String id;

	private String name;

	public Employee(String name) {
		this.name = name;
	}
}
