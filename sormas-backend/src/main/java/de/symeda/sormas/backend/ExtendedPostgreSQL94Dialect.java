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
	// forces the use of the concat function, by default hibernate uses `||` operator
	public final static String CONCAT_FUNCTION = "concat_function";
	public final static String UNACCENT = "unaccent";
	public final static String ILIKE = "ilike";
	public final static String WINDOW_FIRST_VALUE_DESC = "window_first_value_desc";
	public final static String WINDOW_COUNT = "window_count";
	public final static String GREATEST = "greatest";

	public ExtendedPostgreSQL94Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
		registerFunction(ARRAY_TO_STRING, new StandardSQLFunction(ARRAY_TO_STRING));
		registerFunction(CONCAT_FUNCTION, new StandardSQLFunction("concat"));
		registerFunction(ARRAY_AGG, new StandardSQLFunction(ARRAY_AGG));
		registerHibernateType(Types.OTHER, JsonStringType.class.getName());
		registerFunction(SIMILARITY_OPERATOR, new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 % ?2"));
		registerFunction(UNACCENT, new SQLFunctionTemplate(StandardBasicTypes.STRING, "unaccent(?1)"));
		registerFunction(ILIKE, new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 ILIKE ?2"));
		registerFunction(
			WINDOW_FIRST_VALUE_DESC,
			new SQLFunctionTemplate(
				StandardBasicTypes.STRING,
				"FIRST_VALUE(?1) OVER (PARTITION BY ?2 ORDER BY ?3 DESC RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)"));
		registerFunction(
			WINDOW_COUNT,
			new SQLFunctionTemplate(
				StandardBasicTypes.LONG,
				"COUNT(?1) OVER (PARTITION BY ?2 RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)"));
		registerFunction(GREATEST, new StandardSQLFunction(GREATEST));
	}
}
