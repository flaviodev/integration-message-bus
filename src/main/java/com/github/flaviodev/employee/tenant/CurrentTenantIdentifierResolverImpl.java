package com.github.flaviodev.employee.tenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

 private static final String DEFAULT_TENANT_ID = 	"employee_26587a2c89be46b895d6d0f14d182d1a";
	
  @Override
  public String resolveCurrentTenantIdentifier() {
	  String tenantId = TenantContext.getCurrentTenant();
	  
	  if(tenantId!=null) 
		  return "employee_"+tenantId;
	  
	  return DEFAULT_TENANT_ID;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }
}