package com.github.flywaydb.commandline;

import com.github.flywaydb.FlywayEngine;

public class Main {
	protected static final String DEFAULT_CONF = "flywaydb.conf";
	public static void main(String[] args) {
		String conf = DEFAULT_CONF;
		FlywayEngine engine = new FlywayEngine(conf);
		engine.migrate();
	}
}
