package de.symeda.sormas.ui;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class ExtendedH2Dialect extends H2Dialect {

	public final static String ARRAY_TO_STRING = "array_to_string";
	public final static String ARRAY_AGG = "array_agg";
	public final static String UNACCENT = "unaccent";
	public final static String ILIKE = "ilike";

	public ExtendedH2Dialect() {
		super();
		// needed because of hibernate bug: https://hibernate.atlassian.net/browse/HHH-11938
		registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace"));
		registerFunction(ARRAY_TO_STRING, new StandardSQLFunction(ARRAY_TO_STRING));
		registerFunction(ARRAY_AGG, new StandardSQLFunction(ARRAY_AGG));
		registerFunction(ILIKE, new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 ILIKE ?2"));

		// UNACCENT function is not available in H2 database, so let's just make sure it won't fail on tests
		registerFunction(UNACCENT, new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1"));
	}
}
