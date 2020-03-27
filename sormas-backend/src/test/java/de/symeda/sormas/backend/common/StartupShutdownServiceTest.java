package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
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

	@Test
	public void testSormasSchemaVersions() throws Exception {
		assertContinuousSchemaVersions(StartupShutdownService.SORMAS_SCHEMA, 70, 106, 124);
	}

	@Test
	public void testAuditSchemaVersions() throws Exception {
		assertContinuousSchemaVersions(StartupShutdownService.AUDIT_SCHEMA);
	}

	/**
	 * Checks that the order of the updates is correct
	 * 
	 * @param schemaResource
	 * @param omittedVersions
	 * @throws IOException
	 */
	private void assertContinuousSchemaVersions(String schemaResource, int ... omittedVersions)
			throws IOException {
		
		Collection<Integer> omittedVersionsList = Arrays.stream(omittedVersions).mapToObj(i -> i).collect(Collectors.toSet());
		
		try (InputStream schemaStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaResource);
				Scanner scanner = new Scanner(schemaStream, StandardCharsets.UTF_8.name())) {

			int currentVersion = 0;
			
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();

				Integer nextVersion = StartupShutdownService.extractSchemaVersion(nextLine);
				if (nextVersion != null) {
					
					assertThat(nextVersion, Matchers.greaterThan(currentVersion));
					
					for (int v = currentVersion + 1; v < nextVersion; v++) {
						assertThat("Missing version: " + v + " ( found " + nextVersion + " after " + currentVersion + ")", omittedVersionsList.contains(v));
					}
					
					currentVersion = nextVersion; 
				}

			}
		}
	}

}
