package com.github.flaviodev.employee.persistence;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UUIDGenerator implements IdentifierGenerator {

	public static final String PACKAGE_PATH = "com.github.flaviodev.employee.persistence.UUIDGenerator";
	public static final String NAME = "UUIDGenerator";

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return uuid();
	}

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
