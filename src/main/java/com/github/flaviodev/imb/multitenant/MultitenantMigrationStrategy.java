package com.github.flaviodev.imb.multitenant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;

public abstract class MultitenantMigrationStrategy implements FlywayMigrationStrategy {

	@Autowired
	private DataSource defaultDataSource;

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String jdbcUsername;

	@Value("${spring.datasource.password}")
	private String jdbcPassword;

	@Override
	public void migrate(Flyway flyway) {
		flyway.setDataSource(defaultDataSource);
		flyway.setLocations(getDataSourcesLocation());
		flyway.setTarget(MigrationVersion.LATEST);
		flyway.migrate();

		flyway.setLocations(getApplicationLocation());
		Collection<DataSource> dataSources = getDataSources();
		dataSources.forEach(dataSource -> {
			flyway.setBaselineOnMigrate(true);
			flyway.setDataSource(dataSource);
			flyway.migrate();
		});
	}

	private Collection<DataSource> getDataSources() {
		Connection connection = JdbcUtils.openConnection(defaultDataSource);

		ResultSet rs = null;
		Statement statement = null;

		List<DataSource> dataSources = new ArrayList<>();
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery("select datasourceid, driverclassname, name from datasourceconfig");
			while (rs.next()) {
				DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(rs.getString("driverclassname"))
						.username(jdbcUsername).password(jdbcPassword).url(getUrl(rs.getString("name")));
				DataSource ds = factory.build();
				dataSources.add(ds);
			}

			return dataSources;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("error on get all datasources: " + e.getMessage());
		} finally {
			if (rs != null) {
				JdbcUtils.closeResultSet(rs);
			}
			if (statement != null) {
				JdbcUtils.closeStatement(statement);
			}
			JdbcUtils.closeConnection(connection);
		}
	}

	private String getUrl(String name) {
		return jdbcUrl.substring(0, jdbcUrl.lastIndexOf("/")) + "/" + name;
	}

	protected abstract String getDataSourcesLocation();

	protected abstract String getApplicationLocation();
}