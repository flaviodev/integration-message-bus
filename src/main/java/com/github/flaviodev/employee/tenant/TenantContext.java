package com.github.flaviodev.employee.tenant;

import lombok.extern.log4j.Log4j;

@Log4j
public class TenantContext {
	private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

	public static void setCurrentTenant(String tenant) {
		log.debug("Setting current tenant to " + tenant);
		currentTenant.set(tenant);
	}

	public static String getCurrentTenant() {
		return currentTenant.get();
	}

	public static void clear() {
		currentTenant.set(null);
	}
}
