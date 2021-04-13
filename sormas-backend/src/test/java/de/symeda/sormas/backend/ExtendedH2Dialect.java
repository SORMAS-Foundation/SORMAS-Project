package de.symeda.sormas.backend;

import java.sql.Types;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.type.StandardBasicTypes;

public class ExtendedH2Dialect extends H2Dialect {
	public final static String UNACCENT = "unaccent";
	public final static String ILIKE = "ilike";
	public final static String WINDOW_FIRST_VALUE_DESC = "window_first_value_desc";
	public final static String WINDOW_COUNT = "window_count";

	public final static String ARRAY_TO_STRING = "array_to_string";
	public final static String ARRAY_AGG = "array_agg";
	public final static String CONCAT_FUNCTION = "concat_function";

	public ExtendedH2Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
		registerFunction(ARRAY_TO_STRING, new StandardSQLFunction(ARRAY_TO_STRING));
		registerFunction(CONCAT_FUNCTION, new StandardSQLFunction("concat"));
		registerFunction(ARRAY_AGG, new StandardSQLFunction(ARRAY_AGG));
		registerHibernateType(Types.OTHER, JsonBinaryType.class.getName());
		// The function unaccent is specific to PostgreSQL
		// With H2 let's make sure it wont fail by making the function "unaccent" do nothing
		registerFunction(UNACCENT, new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1"));
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
	}
}
