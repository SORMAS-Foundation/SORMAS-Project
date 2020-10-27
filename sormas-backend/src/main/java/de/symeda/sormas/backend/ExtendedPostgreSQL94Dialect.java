package de.symeda.sormas.backend;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

public class ExtendedPostgreSQL94Dialect extends PostgreSQL94Dialect {

	public final static String SIMILARITY_OPERATOR = "similarity_operator";

	public ExtendedPostgreSQL94Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
		registerHibernateType(Types.OTHER, JsonStringType.class.getName());
		registerFunction(SIMILARITY_OPERATOR, new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 % ?2"));
	}
}
