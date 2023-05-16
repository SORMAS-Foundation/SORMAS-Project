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

package de.symeda.junit.cdi.jee.jpa;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;

/**
 * Wrapper for Hibernate to call {@link DatabaseCleaner}.
 */
@RequestScoped
public class HibernateConnectionWrapper implements ConnectionWrapper {

	@Inject
	private Instance<DatabaseCleaner> cleaner;

	@Override
	public void callDatabaseCleaner(EntityManager entityManager) {
		cleanEntityManager(entityManager);
	}

	private void cleanEntityManager(EntityManager entityManager) {
		Object delegate = entityManager.getDelegate();
		if (delegate instanceof SessionImpl) {
			SessionImpl session = (SessionImpl) delegate;
			if (!cleaner.isUnsatisfied()) {
				session.doWork(connection -> cleaner.get().run(connection));
			}
		}
	}
}
