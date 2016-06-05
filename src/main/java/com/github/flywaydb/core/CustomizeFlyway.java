package com.github.flywaydb.core;

import static org.flywaydb.core.internal.util.StringUtils.hasText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogFactory;

public class CustomizeFlyway extends Flyway {
	private static final Log LOG = LogFactory.getLog(Flyway.class);
	protected String moduleName = null; 
	public void configure(String conf) {
		try {
			InputStream confIS = getClassLoader().getResourceAsStream(conf);
			Properties p = new Properties();
			p.load(confIS);
			super.configure(p);
		} catch (Exception e) {
			LOG.error("Can not load the flyway conf file " + conf, e);
		}
	}
	public SmartDriverDataSource getSmartDriverDataSource() {
		return (SmartDriverDataSource) getDataSource();
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(new SmartDriverDataSource((DriverDataSource) dataSource));
	}
	@Override
	public void setDataSource(String url, String user, String password, String... initSqls) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		setDataSource(new SmartDriverDataSource(contextClassLoader, null, url, user, password, initSqls));
	}
	@Override
	public void setLocations(String... locations) {
		SmartDriverDataSource source = getSmartDriverDataSource();
		List<String> newLocations = new ArrayList<String>();
		for (String location : locations) {
			newLocations.add(location + "/" + (hasText(moduleName) ? moduleName + "/" : "") + source.getDialect());
		}
		super.setLocations(newLocations.toArray(new String[locations.length]));
	}
}
