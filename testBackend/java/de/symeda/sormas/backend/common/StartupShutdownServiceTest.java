package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

	private String[] supportedDatabaseVersions = new String[] {
		"9.5",
		"9.5.25",
		"9.6.5",
		"9.6",
		"10.1",
		"10.14 (Ubuntu 10.14-1.pgdg20.04+1)" };

	private String[] unsupportedDatabaseVersions = new String[] {
		"8.4",
		"8.4.22",
		"9.1",
		"11.0" };

	@Test
	public void testIsSupportedDatabaseVersion() {

		for (String version : supportedDatabaseVersions) {
			assertTrue(
				String.format("Supported version not recognized correctly: '%s'", version),
				StartupShutdownService.isSupportedDatabaseVersion(version));
		}

		for (String version : unsupportedDatabaseVersions) {
			assertFalse(
				String.format("Unsupported version not recognized correctly: '%s'", version),
				StartupShutdownService.isSupportedDatabaseVersion(version));
		}
	}

	@Test
	public void testIsBlankOrSqlComment() {

		assertThat(StartupShutdownService.isBlankOrSqlComment(""), is(true));
		assertThat(StartupShutdownService.isBlankOrSqlComment(" \t  "), is(true));
		assertThat(
			StartupShutdownService.isBlankOrSqlComment("--INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"),
			is(true));
		assertThat(StartupShutdownService.isBlankOrSqlComment("\t   --"), is(true));

		assertThat(
			StartupShutdownService.isBlankOrSqlComment("INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"),
			is(false));
		assertThat(StartupShutdownService.isBlankOrSqlComment("  select * from test; -- comment"), is(false));
	}

	@Test
	public void testExtractSchemaVersion() {

		assertThat(StartupShutdownService.extractSchemaVersion(null), nullValue());
		assertThat(StartupShutdownService.extractSchemaVersion(""), nullValue());
		assertThat(StartupShutdownService.extractSchemaVersion("select * from test;"), nullValue());
		assertThat(
			StartupShutdownService.extractSchemaVersion("--INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"),
			nullValue());

		assertThat(
			StartupShutdownService.extractSchemaVersion("INSERT INTO schema_version (version_number, comment) VALUES (1, 'Init database');"),
			is(1));
		assertThat(
			StartupShutdownService.extractSchemaVersion(
				"   \tINSERT   \tINTO   \tschema_version(   \tversion_number   \t,comment   \t)VALUES( 123543,'Init database');"),
			is(123543));
	}

	@Test
	public void testSormasSchemaVersions() throws IOException {
		assertContinuousSchemaVersions(StartupShutdownService.SORMAS_SCHEMA, 70, 106, 124);
	}

	@Test
	public void testAuditSchemaVersions() throws IOException {
		assertContinuousSchemaVersions(StartupShutdownService.AUDIT_SCHEMA);
	}

	/**
	 * Checks that the order of the updates is correct
	 * 
	 * @param schemaResource
	 * @param omittedVersions
	 * @throws IOException
	 */
	private void assertContinuousSchemaVersions(String schemaResource, int... omittedVersions) throws IOException {

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
						assertThat(
							"Missing version: " + v + " ( found " + nextVersion + " after " + currentVersion + ")",
							omittedVersionsList.contains(v));
					}

					currentVersion = nextVersion;
				}
			}
		}
	}
}
