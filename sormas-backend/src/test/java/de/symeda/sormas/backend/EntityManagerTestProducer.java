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

	public static final String BEAN_TEST_PU = "beanTestPU";

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
