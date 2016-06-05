package com.github.flywaydb;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.github.flywaydb.DbMigration;
import com.github.flywaydb.FlywayEngine;
import com.github.flywaydb.core.CustomizeFlyway;
import com.github.flywaydb.utils.ClassUtils;

import junit.framework.TestCase;

public class AppTest extends TestCase {

	@Test
	public void test() throws Exception {
		String url = "jdbc:mysql://localhoflywaydbflywaydbst:3306/flywaydb?useUnicode=true&characterEncoding=UTF-8";
		String user = "flywaydb";
		String password = "flywaydb";
		String initSqls = "select '1'";
		migrate(url, user, password, initSqls, "classpath:db/migration", "module2", "schema_version_om");
		migrate(url, user, password, initSqls, "classpath:db/migration", "module3", "schema_version_om_2");
		migrate(url, user, password, initSqls, "classpath:db/migration", "module1", "schema_version");
	}

	/**
	 * @param url
	 * @param user
	 * @param password
	 * @param initSqls
	 */
	public void migrate(String url, String user, String password, String initSqls, String location, String module, String table) {
		CustomizeFlyway myFlyway = new CustomizeFlyway();
		myFlyway.setDataSource(url, user, password, initSqls);
		myFlyway.setModuleName(module);
		myFlyway.setCleanOnValidationError(true);
		myFlyway.setBaselineOnMigrate(true);
		myFlyway.setLocations(location);
		myFlyway.setBaselineVersionAsString("0");
		myFlyway.setTable(table);
		myFlyway.migrate();
	}
	@Test
	public void testC() throws Exception {
		List<Class<?>> findAnnotations = ClassUtils.findClassByAnnotation(DbMigration.class);
		System.out.println(findAnnotations.size());
		List<DbMigration> annotations = ClassUtils.findAnnotations(DbMigration.class);
		System.out.println(annotations.size());
		Collections.sort(annotations, new Comparator<DbMigration>() {
			@Override public int compare(DbMigration o1, DbMigration o2) {
				return o1.level() - o2.level();
			}
		});
		for (DbMigration dm : annotations) {
			System.out.println("module: " + dm.moduleName() + " level: " + dm.level());
		}
		for (String loc : annotations.get(0).locations()) {
			System.out.println("location: " + loc);
		}
	}
	@Test
	public void testMigrate() throws Exception {
		String conf = "flywaydb.conf";
		FlywayEngine engine = new FlywayEngine(conf);
		engine.migrate();
	}
}
