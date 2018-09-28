package com.github.flaviodev.imb.multitenant;

import java.util.List;

public interface DataSourceConfigRepository {

	DataSourceConfig findByName(String name);

	List<DataSourceConfig> findAll();

}