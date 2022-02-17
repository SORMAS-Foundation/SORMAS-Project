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
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import info.novatec.beantest.api.BaseBeanTest;

public class StartupShutdownServiceTest extends BaseBeanTest {

	private final String[] SUPPORTED_DATABASE_VERSIONS = new String[] {
		"9.5",
		"9.5.25",
		"9.6.5",
		"9.6",
		"10.1",
		"10.14 (Ubuntu 10.14-1.pgdg20.04+1)" };

	private final String[] UNSUPPORTED_DATABASE_VERSIONS = new String[] {
		"8.4",
		"8.4.22",
		"9.1",
		"11.0" };

	@Test
	public void testIsSupportedDatabaseVersion() {

		for (String version : SUPPORTED_DATABASE_VERSIONS) {
			assertTrue(
				String.format("Supported version not recognized correctly: '%s'", version),
				StartupShutdownService.isSupportedDatabaseVersion(version));
		}

		for (String version : UNSUPPORTED_DATABASE_VERSIONS) {
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
	public void testHistoryTablesMatch() throws IOException, URISyntaxException {

		SormasPostgresSQLContainer container = new SormasPostgresSQLContainer().withDatabaseName("sormas");
		container.start();

		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", container.getJdbcUrl());
		properties.put("javax.persistence.jdbc.user", container.getUsername());
		properties.put("javax.persistence.jdbc.password", container.getPassword());
		properties.put("javax.persistence.jdbc.driver", container.getDriverClassName());
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
		properties.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform");
		properties.put("hibernate.jdbc.batch_size", "100");
		properties.put("hibernate.order_inserts", "true");
		properties.put("hibernate.order_updates", "true");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("beanTestPU", properties);
		EntityManager em = emf.createEntityManager();

		String checkHistoryTablesSql = new String(
			Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("checkHistoryTables.sql")).toURI())));
		@SuppressWarnings("unchecked")
		List<Object[]> results = (List<Object[]>) em.createNativeQuery(checkHistoryTablesSql).getResultList();
		assertTrue(CollectionUtils.isEmpty(results));

	}

	/**
	 * Checks that the order of the updates is correct
	 *
	 * @param schemaResource
	 *            {@link StartupShutdownService#SORMAS_SCHEMA} or {@link StartupShutdownService#AUDIT_SCHEMA}
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
								"Missing version: " + v + " ( found " + nextVersion + " after " + currentVersion + ")",
								omittedVersionsList.contains(v));
						}

						currentVersion = nextVersion;
					}
				}
			}
		}
	}

	public static class SormasPostgresSQLContainer extends JdbcDatabaseContainer<SormasPostgresSQLContainer> {

		private String databaseName;

		public SormasPostgresSQLContainer() {
			super(
				new ImageFromDockerfile().withFileFromClasspath("setup_sormas_db.sh", "testcontainers/setup_sormas_db.sh")
					.withFileFromClasspath("sormas_schema.sql", "sql/sormas_schema.sql")
					.withFileFromClasspath("Dockerfile", "testcontainers/Dockerfile"));
			this.waitStrategy = new LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*\\s")
				.withTimes(2)
				.withStartupTimeout(Duration.of(60, SECONDS));
			addExposedPort(POSTGRESQL_PORT);
			withEnv("POSTGRES_USER", getUsername());
			withEnv("POSTGRES_PASSWORD", getPassword());
		}

		@Override
		public String getDriverClassName() {
			return "org.postgresql.Driver";
		}

		@Override
		public String getJdbcUrl() {
			String additionalUrlParams = constructUrlParameters("?", "&");
			return "jdbc:postgresql://" + getContainerIpAddress() + ":" + getMappedPort(POSTGRESQL_PORT) + "/" + getDatabaseName()
				+ additionalUrlParams;
		}

		@Override
		public SormasPostgresSQLContainer withDatabaseName(String dbName) {
			this.databaseName = dbName;
			return self();
		}

		@Override
		public String getDatabaseName() {
			return databaseName;
		}

		@Override
		public String getUsername() {
			return "sormas_user";
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
