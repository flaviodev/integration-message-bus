package com.github.flaviodev.imb.persistence;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.github.flaviodev.imb.model.base.EntityBase;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("deprecation")
public class Dao {

	private static EntityManagerFactory factory;

	private static EntityManagerFactory getEntityManagerFactory() {

		if (factory == null)
			factory = Persistence.createEntityManagerFactory("dpPersistenceUnity");

		return factory;
	}

	public static EntityManager getEntityManager() {

		return getEntityManagerFactory().createEntityManager();
	}

	public static <I extends Serializable, E extends EntityBase<I>> Criteria createCriteria(Class<E> classeEntidade,
			String alias) {

		return ((Session) getEntityManager().getDelegate()).createCriteria(classeEntidade, alias);
	}

	public static <I extends Serializable, E extends EntityBase<I>> Criteria createCriteria(Class<E> classeEntidade) {

		return ((Session) getEntityManager().getDelegate()).createCriteria(classeEntidade);
	}

	public static String getAlias(String... aliases) {

		if (aliases == null || aliases.length == 0)
			return null;

		StringBuilder aliasesContatenados = new StringBuilder(aliases[0]);
		boolean primeiro = true;

		for (String alias : aliases) {

			if (primeiro) {

				primeiro = false;
			} else {

				aliasesContatenados.append('.');
				aliasesContatenados.append(alias);
			}
		}

		return aliasesContatenados.toString();
	}
}
