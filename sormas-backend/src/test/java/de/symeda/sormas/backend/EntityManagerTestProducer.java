package de.symeda.sormas.backend;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import de.hilling.junit.cdi.annotations.GlobalTestImplementation;
import de.hilling.junit.cdi.scope.TestSuiteScoped;
import de.symeda.junit.cdi.jee.EntityManagerResourcesProvider;

/**
 * Producer for EntityManagers used in cdi-test unit tests.
 */

@TestSuiteScoped
public class EntityManagerTestProducer {

	/**
	 * The persistence unit bean tests run against. Defaults to the in-memory H2 unit;
	 * {@code -Dsormas.test.pu=beanTestPgPU} switches the whole CDI stack to a PostgreSQL
	 * container instead, for tests that exercise native SQL H2 cannot run.
	 */
	public static final String BEAN_TEST_PU = System.getProperty("sormas.test.pu", "beanTestPU");

	private static final String POSTGRES_PU = "beanTestPgPU";

	/**
	 * Whether the bean tests are running against PostgreSQL rather than the default in-memory H2.
	 *
	 * <p>
	 * Only setup that is dialect-specific should ask. Everything a test asserts should hold on
	 * either database; where it cannot - native SQL H2 will not parse - the test carries
	 * {@code @Tag("postgres")} and does not run on H2 at all.
	 */
	public static boolean isPostgres() {
		return POSTGRES_PU.equals(BEAN_TEST_PU);
	}

	@Inject
	private EntityManagerResourcesProvider entityManagerProvider;

	@Produces
	@GlobalTestImplementation
	@RequestScoped
	protected EntityManagerFactory provideTestEntityManagerFactory() {
		return entityManagerProvider.resolveEntityManagerFactory(BEAN_TEST_PU);
	}

	@Produces
	@GlobalTestImplementation
	@RequestScoped
	protected EntityManager provideTestEntityManager() {
		return entityManagerProvider.resolveEntityManager(BEAN_TEST_PU);
	}
}
