package com.github.flywaydb.core;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;

public class SmartDriverDataSource extends DriverDataSource {
	protected String dialect;
	public SmartDriverDataSource(ClassLoader classLoader, String driverClass,
		String url, String user, String password, String[] initSqls) throws FlywayException {
		super(classLoader, driverClass, url, user, password, initSqls);
		String[] urlInfos = url.split(":");
		dialect = urlInfos[1].toLowerCase();
	}
	public SmartDriverDataSource(DriverDataSource datasource) {
		super(Thread.currentThread().getContextClassLoader(), null, datasource.getUrl(),
			datasource.getUser(), datasource.getPassword(), datasource.getInitSqls()
		);
		String[] urlInfos = datasource.getUrl().split(":");
		dialect = urlInfos[1].toLowerCase();
	}
	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}
}
