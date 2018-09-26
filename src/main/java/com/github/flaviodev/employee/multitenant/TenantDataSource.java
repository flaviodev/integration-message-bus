package com.github.flaviodev.employee.multitenant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("springContext")
public class TenantDataSource implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, DataSource> dataSources = new HashMap<>();

	@Autowired
	private DataSourceConfigRepository configRepo;

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String jdbcUsername;

	@Value("${spring.datasource.password}")
	private String jdbcPassword;

	public DataSource getDataSource(String name) {
		if (dataSources.get(name) != null) {
			return dataSources.get(name);
		}
		DataSource dataSource = createDataSource(name);
		if (dataSource != null) {
			dataSources.put(name, dataSource);
		}
		return dataSource;
	}

	public Map<String, DataSource> getAll() {
		List<DataSourceConfig> configList = configRepo.findAll();
		Map<String, DataSource> result = new HashMap<>();
		for (DataSourceConfig config : configList) {
			DataSource dataSource = getDataSource(config.getName());
			result.put(config.getName(), dataSource);
		}
		return result;
	}

	private DataSource createDataSource(String name) {
		DataSourceConfig config = configRepo.findByName(name);
		if (config != null) {
			DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(config.getDriverClassName())
					.username(jdbcUsername).password(jdbcPassword).url(getUrl(config));
			DataSource ds = factory.build();
			return ds;
		}
		return null;
	}

	private String getUrl(DataSourceConfig config) {
		return jdbcUrl.substring(0, jdbcUrl.lastIndexOf("/")) + "/" + config.getName();
	}
}