package com.github.flywaydb;

import static org.flywaydb.core.internal.util.StringUtils.tokenizeToStringArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogFactory;

import com.github.flywaydb.core.CustomizeFlyway;
import com.github.flywaydb.utils.ClassUtils;

public class FlywayEngine {
	private static final Log LOG = LogFactory.getLog(FlywayEngine.class);

	private String conf;
	protected Properties configuration;
	protected List<String> regModules;
	protected Map<String, List<String>> locations;
	protected List<String> defaultLocations;

	public FlywayEngine(String conf) {
		this.conf = conf;
		loadConf();
		loadCustomizeConf();
	}

	private void loadCustomizeConf() {
		List<DbMigration> annotations = ClassUtils.findAnnotations(DbMigration.class);
		regModules = new ArrayList<String>();
		locations = new HashMap<String, List<String>>();
		Collections.sort(annotations, new Comparator<DbMigration>() {
			@Override public int compare(DbMigration o1, DbMigration o2) {
				int level = o1.level() - o2.level();
				return level == 0 ? o1.ordered() - o2.ordered() : level;
			}
		});
		for (DbMigration mig : annotations) {
			LOG.info("Module: " + mig.moduleName() + " Level: " + mig.level() + " Ordered: " + mig.ordered());
		}
		for (DbMigration mig : annotations) {
			String module = mig.moduleName();
			String[] locates = mig.locations();
			if (StringUtils.hasText(module)) {
				regModules.add(module);
			}
			if (null != locates && locates.length > 0) {
				locations.put(module, Arrays.asList(locates));
			} else {
				locations.put(module, defaultLocations);
			}
		}
	}

	public void loadConf() {
		try {
			defaultLocations = new ArrayList<String>();
			InputStream confIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(conf);
			configuration = new Properties();
			configuration.load(confIS);
			String locationsProp = configuration.getProperty("flyway.locations");
			if (locationsProp == null) {
				defaultLocations.add("db/migration");
			} else {
				defaultLocations.addAll(Arrays.asList(tokenizeToStringArray(locationsProp, ",")));
			}
			configuration.setProperty("flyway.locations", "");
		} catch (Exception e) {
			LOG.error("Can not load the flyway conf file " + conf, e);
		}
	}

	public void migrate() {
		if (regModules.isEmpty()) LOG.info("No db script of modules to run.");
		for (String module : regModules) {
			LOG.info("========Start to migration " + module + "=========");
			CustomizeFlyway myFlyway = new CustomizeFlyway();
			myFlyway.setModuleName(module); // Must set before the configure method
			myFlyway.configure(configuration);
			myFlyway.setLocations(locations.get(module).toArray(new String[0]));
			myFlyway.setCleanOnValidationError(false);
			myFlyway.setBaselineOnMigrate(true);
			myFlyway.setBaselineVersionAsString("0");
			myFlyway.setTable("sch_ver_" + module);
			myFlyway.migrate();
			LOG.info("========End of migration " + module + "=========");
		}
	}
}
