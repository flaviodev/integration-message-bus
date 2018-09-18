package com.github.flaviodev.employee.tenanty;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class MultiTenantConfig {

	@Autowired
	MultiTenantConnectionProvider dsProvider;

	@Autowired
	CurrentTenantIdentifierResolver tenantResolver;

	@Autowired
	AutowireCapableBeanFactory beanFactory;

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@PersistenceContext
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
		Map<String, Object> props = new HashMap<>();
		props.put("hibernate.multiTenancy", MultiTenancyStrategy.DATABASE.name());
		props.put("hibernate.multi_tenant_connection_provider", dsProvider);
		props.put("hibernate.tenant_identifier_resolver", tenantResolver);

		LocalContainerEntityManagerFactoryBean result = builder.dataSource(dataSource()).persistenceUnit("employee")
				.properties(props)
				.packages("com.github.flaviodev.employee.model", "com.github.flaviodev.employee.multitenant").build();
		result.setJpaVendorAdapter(jpaVendorAdapter());
		return result;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setGenerateDdl(false);
		return hibernateJpaVendorAdapter;
	}

}