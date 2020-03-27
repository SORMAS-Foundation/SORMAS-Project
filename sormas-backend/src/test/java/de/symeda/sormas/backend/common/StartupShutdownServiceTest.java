package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

public class StartupShutdownServiceTest {
	
	@Test
	public void testIsBlankOrSqlComment() {
		assertThat(StartupShutdownService.isBlankOrSqlComment(""), is(true));
		assertThat(StartupShutdownService.isBlankOrSqlComment(" \t  "), is(true));
		assertThat(StartupShutdownService.isBlankOrSqlComment("--INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"), is(true));
		assertThat(StartupShutdownService.isBlankOrSqlComment("\t   --"), is(true));

		assertThat(StartupShutdownService.isBlankOrSqlComment("INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"), is(false));
		assertThat(StartupShutdownService.isBlankOrSqlComment("  select * from test; -- comment"), is(false));
	}

	@Test
	public void testExtractSchemaVersion() {
		assertThat(StartupShutdownService.extractSchemaVersion(null), nullValue());
		assertThat(StartupShutdownService.extractSchemaVersion(""), nullValue());
		assertThat(StartupShutdownService.extractSchemaVersion("select * from test;"), nullValue());
		assertThat(StartupShutdownService.extractSchemaVersion("--INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"), nullValue());

		assertThat(StartupShutdownService.extractSchemaVersion("INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"), is(1));
		assertThat(StartupShutdownService.extractSchemaVersion("   \tINSERT   \tINTO   \tschema_version(   \tversion_number   \t,comment   \t)VALUES( 123543,'Init database');"), is(123543));
	}

}
