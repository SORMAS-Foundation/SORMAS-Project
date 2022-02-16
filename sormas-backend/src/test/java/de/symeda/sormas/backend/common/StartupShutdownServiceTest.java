package de.symeda.sormas.backend.common;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import info.novatec.beantest.api.BaseBeanTest;

public class StartupShutdownServiceTest extends BaseBeanTest {

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

	@Test
	public void testHistoryTablesMatch() {

		Map<String, String> env = new HashMap<>();
		env.put("SORMAS_POSTGRES_USER", "sormas_user");
		env.put("SORMAS_POSTGRES_PASSWORD", "password");
		env.put("DB_NAME", "sormas");
		env.put("DB_NAME_AUDIT", "sormas_audit");
		env.put("POSTGRES_PASSWORD", "password");
		env.put("POSTGRES_USER", "postgres");

		// temporal table now working
		new SormasPostgresSQLContainer<>(
			new ImageFromDockerfile().withFileFromClasspath("setup_sormas.sh", "db/setup_sormas.sh")
				.withFileFromClasspath("Dockerfile", "db/Dockerfile")).withEnv(env).withDatabaseName("sormas")
					.withInitScript("sql/sormas_schema.sql") // create schema and add test data
					.start();
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

	public static class SormasPostgresSQLContainer<SELF extends SormasPostgresSQLContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

		private String databaseName;

		public SormasPostgresSQLContainer(final Future<String> image) {
			super(image);
			this.waitStrategy = new LogMessageWaitStrategy()
					.withRegEx(".*database system is ready to accept connections.*\\s")
					.withTimes(2)
					.withStartupTimeout(Duration.of(60, SECONDS));
			addExposedPort(POSTGRESQL_PORT);
		}

		@Override
		public String getDriverClassName() {
			return "org.postgresql.Driver";
		}

		@Override
		public String getJdbcUrl() {
			String additionalUrlParams = constructUrlParameters("?", "&");
			return "jdbc:postgresql://" + getContainerIpAddress() + ":" + getMappedPort(POSTGRESQL_PORT) + "/" + getDatabaseName() + additionalUrlParams;
		}

		@Override
		public SELF withDatabaseName(String dbName) {
			this.databaseName = dbName;
			return self();
		}

		@Override
		public String getDatabaseName() {
			return databaseName;
		}

		@Override
		public String getUsername() {
			return "postgres";
		}

		@Override
		public String getPassword() {
			return "password";
		}

		@Override
		protected String getTestQueryString() {
			return "SELECT 1";
		}

		@Override
		protected void waitUntilContainerStarted() {
			getWaitStrategy().waitUntilReady(this);
		}
	}
}
