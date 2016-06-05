package com.github.flywaydb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbMigration {
	String moduleName() default "";
	String[] locations() default {};
	int level() default 1;
	int ordered() default 1;
}
