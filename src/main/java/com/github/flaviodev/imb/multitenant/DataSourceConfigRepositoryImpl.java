package com.github.flaviodev.imb.multitenant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DataSourceConfigRepositoryImpl implements DataSourceConfigRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public DataSourceConfig findByName(String name) {
		try {
			return jdbcTemplate.queryForObject(
					"select datasourceid, driverclassname, url, name from datasourceconfig where name = ?",
					new Object[] { name }, new BeanPropertyRowMapper<DataSourceConfig>(DataSourceConfig.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DataSourceConfig> findAll() {
		return jdbcTemplate.query("select datasourceid, driverclassname, url, name from datasourceconfig",
				new BeanPropertyRowMapper<DataSourceConfig>(DataSourceConfig.class));
	}
}