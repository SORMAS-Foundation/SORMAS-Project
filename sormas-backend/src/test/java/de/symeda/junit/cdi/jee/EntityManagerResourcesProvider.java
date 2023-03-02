/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ---------------------------------------------------------------------
 * Based on cdi-test: https://github.com/guhilling/cdi-test
 * Licensed under the Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */

package de.symeda.junit.cdi.jee;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.hilling.junit.cdi.scope.TestSuiteScoped;

/**
 * Provider/Factory for {@link EntityManager}s used in cdi-test unit tests.
 * <br>
 * Lookup you resources ({@link EntityManager} and {@link EntityManagerFactory} using this class. Transactions and cleanup will be handled
 * automatically when unit tests are started and finished.
 */
@TestSuiteScoped
public class EntityManagerResourcesProvider {

	private Map<String, EntityManagerFactory> factories = new HashMap();
	@Inject
	private TestEntityResources testEntityResources;
	@Inject
	private BeanManager beanManager;

	public EntityManagerResourcesProvider() {
	}

	public synchronized EntityManagerFactory resolveEntityManagerFactory(String persistenceUnitName) {
		return (EntityManagerFactory) this.factories.computeIfAbsent(persistenceUnitName, this::createEntityManagerFactory);
	}

	private EntityManagerFactory createEntityManagerFactory(String persistenceUnit) {
		Map<String, Object> props = new HashMap();
		props.put("javax.persistence.bean.manager", this.beanManager);
		return Persistence.createEntityManagerFactory(persistenceUnit, props);
	}

	public synchronized EntityManager resolveEntityManager(String persistenceUnitName) {
		if (!this.testEntityResources.hasEntityManager(persistenceUnitName)) {
			this.testEntityResources
				.putEntityManager(persistenceUnitName, this.resolveEntityManagerFactory(persistenceUnitName).createEntityManager());
		}

		return this.testEntityResources.getEntityManager(persistenceUnitName);
	}
}
