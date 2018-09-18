package com.github.flaviodev.employee.multitenant;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.github.flaviodev.employee.SpringContext;

@Component
@DependsOn("springContext")
public class DataSourceMultiTenantConnectionImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

  private static final long serialVersionUID = 1L;

  String DEFAULT_TENANT_ID = "default";
  String CURRENT_TENANT_IDENTIFIER = "CURRENT_TENANT_IDENTIFIER";

  @Autowired
  ApplicationContext context;

  static Map<String, DataSource> map = new HashMap<>();

  boolean init = false;

  @Override
  protected DataSource selectAnyDataSource() {
    DataSource defaultDS = map.get(DEFAULT_TENANT_ID);
    if (defaultDS == null) {
      defaultDS = context.getBean(DataSource.class);
      map.put(DEFAULT_TENANT_ID, defaultDS);
    }
    return defaultDS;
  }

  @Override
  protected DataSource selectDataSource(String tenantIdentifier) {
    if (!init) {
      synchronized (this) {
        init = true;
        TenantDataSource tenantDataSource = context.getBean(TenantDataSource.class);
        Map<String, DataSource> dataSources = tenantDataSource.getAll();
        map.putAll(dataSources);
      }
    }
    return map.get(tenantIdentifier);
  }
}