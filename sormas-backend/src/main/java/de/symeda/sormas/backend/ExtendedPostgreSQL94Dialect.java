package de.symeda.sormas.backend;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

public class ExtendedPostgreSQL94Dialect extends PostgreSQL94Dialect {

	public ExtendedPostgreSQL94Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
	}
}
