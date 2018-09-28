package com.github.flaviodev.imb.model.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.Transient;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.flaviodev.imb.SpringContext;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("unchecked")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class EntittyBaseCRUD<I extends Serializable, E extends EntityBase<I>, R extends JpaRepository<E, I>>
		extends EntityBase<I> {

	private static final long serialVersionUID = 8260439965955361380L;

	@Transient
	private R repository;

	private Class<R> getRepositoryClass() {
		return (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}

	protected R getRepository() {
		if (repository == null)
			repository = SpringContext.getRepository(getRepositoryClass());

		return repository;
	}

	@Transactional
	public E save() {
		return getRepository().save((E) this);
	}

	@Transactional
	public void delete() {
		getRepository().delete((E) this);
	}
}
