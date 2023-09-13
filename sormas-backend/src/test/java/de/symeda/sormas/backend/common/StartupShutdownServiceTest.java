package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class StartupShutdownServiceTest {

	private final String[] SUPPORTED_DATABASE_VERSIONS = new String[] {
		"14.0",
		"15.0" };

	private final String[] UNSUPPORTED_DATABASE_VERSIONS = new String[] {
		"8.4",
		"8.4.22",
		"9.1",
		"9.5",
		"9.5.25",
		"9.6.5",
		"9.6",
		"10.1",
		"10.14 (Ubuntu 10.14-1.pgdg20.04+1)",
		"11.0" };

	@Test
	public void testIsSupportedDatabaseVersion() {

		for (String version : SUPPORTED_DATABASE_VERSIONS) {
			assertTrue(
				StartupShutdownService.isSupportedDatabaseVersion(version),
				String.format("Supported version not recognized correctly: '%s'", version));
		}

		for (String version : UNSUPPORTED_DATABASE_VERSIONS) {
			assertFalse(
				StartupShutdownService.isSupportedDatabaseVersion(version),
				String.format("Unsupported version not recognized correctly: '%s'", version));
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

	/**
	 * Checks that the order of the updates is correct
	 *
	 * @param schemaResource
	 *            {@link StartupShutdownService#SORMAS_SCHEMA}
	 * @param omittedVersions
	 *            versions to skip
	 */
	private void assertContinuousSchemaVersions(String schemaResource, int... omittedVersions) throws IOException {

		Collection<Integer> omittedVersionsList = Arrays.stream(omittedVersions).boxed().collect(Collectors.toSet());

		try (InputStream schemaStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaResource)) {
			try (Scanner scanner = new Scanner(Objects.requireNonNull(schemaStream), StandardCharsets.UTF_8.name())) {

				int currentVersion = 0;

				while (scanner.hasNextLine()) {
					String nextLine = scanner.nextLine();

					Integer nextVersion = StartupShutdownService.extractSchemaVersion(nextLine);
					if (nextVersion != null) {

						assertThat(nextVersion, Matchers.greaterThan(currentVersion));

						for (int v = currentVersion + 1; v < nextVersion; v++) {
							assertTrue(
								omittedVersionsList.contains(v),
								"Missing version: " + v + " ( found " + nextVersion + " after " + currentVersion + ")");
						}

						currentVersion = nextVersion;
					}
				}
			}
		}
	}
}
