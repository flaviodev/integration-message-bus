package com.github.flaviodev.employee;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.github.flaviodev.employee.model.base.EntityBase;

@Component
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;

	public static ApplicationContext get() {
		return context;
	}

	public static <T> T getBean(Class<T> bean) {
		return context.getBean(bean);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static <R extends JpaRepository<E, I>, E extends EntityBase<I>, I extends Serializable> R getRepository(
			Class<R> classeRepositorio) {

		return context.getBean(classeRepositorio);
	}

}