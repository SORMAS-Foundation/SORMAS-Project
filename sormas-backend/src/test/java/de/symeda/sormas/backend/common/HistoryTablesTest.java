package de.symeda.sormas.backend.common;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeNoException;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import de.hilling.junit.cdi.CdiTestJunitExtension;
import de.hilling.junit.cdi.annotations.BypassTestInterceptor;

@ExtendWith(CdiTestJunitExtension.class)
public class HistoryTablesTest {

	/**
	 * Test that the *_history tables have the same columns as the corresponding production tables
	 *
	 * TROUBLESHOOTING
	 * - In case the container can't be started:
	 * https://github.com/sormas-foundation/SORMAS-Project/issues/9177#issuecomment-1125130020
	 *
	 * @throws IOException
	 *             if the resource(s) used in the test can't be read
	 * @throws URISyntaxException
	 *             if the path to a resource is not syntactically correct
	 */
	@Test
	public void testHistoryTablesMatch() throws IOException, URISyntaxException {

		SormasPostgresSQLContainer container = new SormasPostgresSQLContainer();
		start(container);

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
		properties.put("hibernate.hbm2ddl.auto", "none");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("beanTestPU", properties);
		EntityManager em = emf.createEntityManager();

		String checkHistoryTablesSql = new String(
			Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("checkHistoryTables.sql")).toURI())));
		@SuppressWarnings("unchecked")
		List<Object[]> results = (List<Object[]>) em.createNativeQuery(checkHistoryTablesSql).getResultList();
		StringBuilder result = new StringBuilder();
		results.forEach(objects -> {
			result.append("\n");
			Arrays.stream(objects).forEach(o -> result.append((o != null ? o.toString() : "") + " "));
		});
		assertTrue(CollectionUtils.isEmpty(results), result.toString());
	}

	private void start(SormasPostgresSQLContainer container) {

		try {
			container.start();
		} catch (IllegalStateException e) {
			assertThat(e.getMessage(), equalTo("Could not find a valid Docker environment. Please see logs and check configuration"));
			assumeNoException("Could not find a valid Docker environment, skipping test", e);
		}
	}

	@BypassTestInterceptor
	public static class SormasPostgresSQLContainer extends JdbcDatabaseContainer<SormasPostgresSQLContainer> {

		public SormasPostgresSQLContainer() {
			super(
				new ImageFromDockerfile().withFileFromClasspath("setup_sormas_db.sh", "testcontainers/setup_sormas_db.sh")
					.withFileFromClasspath("versioning_function.sql", "sql/temporal_tables/versioning_function.sql")
					.withFileFromClasspath("sormas_schema.sql", "sql/sormas_schema.sql")
					.withFileFromClasspath("Dockerfile", "testcontainers/Dockerfile"));
			this.waitStrategy = new LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*\\s")
				.withTimes(2)
				.withStartupTimeout(Duration.of(60, SECONDS));
			addExposedPort(5432);
			withEnv("POSTGRES_USER", getUsername());
			withEnv("POSTGRES_PASSWORD", getPassword());
			withEnv("POSTGRES_DB", getDatabaseName());
		}

		@Override
		public String getDriverClassName() {
			return "org.postgresql.Driver";
		}

		@Override
		public String getJdbcUrl() {
			String additionalUrlParams = constructUrlParameters("?", "&");
			return "jdbc:postgresql://" + getContainerIpAddress() + ":" + getMappedPort(5432) + "/" + getDatabaseName() + additionalUrlParams;
		}

		@Override
		public String getDatabaseName() {
			return "sormas";
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
