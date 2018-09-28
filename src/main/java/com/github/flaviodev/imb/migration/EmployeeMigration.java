package com.github.flaviodev.imb.migration;

import org.springframework.stereotype.Component;

import com.github.flaviodev.imb.multitenant.MultitenantMigrationStrategy;

@Component
public class EmployeeMigration extends MultitenantMigrationStrategy {

	@Override
	protected String getDataSourcesLocation() {
		return "classpath:migration/datasource";
	}

	@Override
	protected String getApplicationLocation() {
		return "classpath:migration/employee";
	}
}