package de.symeda.sormas.backend;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

public class ExtendedPostgreSQL94Dialect extends PostgreSQL94Dialect {

	public final static String SIMILARITY_OPERATOR = "similarity_operator";
	public final static String ARRAY_TO_STRING = "array_to_string";
	public final static String ARRAY_AGG = "array_agg";

	public ExtendedPostgreSQL94Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
		registerFunction(ARRAY_TO_STRING, new StandardSQLFunction(ARRAY_TO_STRING));
		registerFunction(ARRAY_AGG, new StandardSQLFunction(ARRAY_AGG));
		registerHibernateType(Types.OTHER, JsonStringType.class.getName());
		registerFunction(SIMILARITY_OPERATOR, new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 % ?2"));
	}
}
