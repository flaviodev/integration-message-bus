package com.github.flaviodev.imb.tenant;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TenantFilter implements Filter {

	//@Bean
	public TenantFilter createTenantFilter() {
		return new TenantFilter();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String tenantId = request.getParameter("tenantId");
		TenantContext.setCurrentTenant(tenantId);

		chain.doFilter(request, response);

		TenantContext.clear();

	}

	@Override
	public void destroy() {
	}
}