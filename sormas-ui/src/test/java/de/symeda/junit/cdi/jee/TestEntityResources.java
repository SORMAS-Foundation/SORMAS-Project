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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.jupiter.api.extension.ExtensionContext;

import de.hilling.junit.cdi.lifecycle.TestEvent;
import de.hilling.junit.cdi.scope.TestScoped;
import de.hilling.junit.cdi.scope.TestState;
import de.symeda.junit.cdi.jee.jpa.ConnectionWrapper;

/**
 * Track created {@link EntityManager} and dispose them after Tests.
 */
@TestScoped
public class TestEntityResources {

	private Map<String, EntityManager> entityManagers = new HashMap();
	@Inject
	private Instance<ConnectionWrapper> connectionWrappers;

	public TestEntityResources() {
	}

	public boolean hasEntityManager(String name) {
		return this.entityManagers.containsKey(name);
	}

	public EntityManager getEntityManager(String name) {
		return (EntityManager) this.entityManagers.get(name);
	}

	protected void finishResources(@Observes @TestEvent(TestState.FINISHING) ExtensionContext description) {
		this.entityManagers.values().forEach(EntityManager::close);
		this.entityManagers.clear();
	}

	public void putEntityManager(String name, EntityManager entityManager) {
		this.entityManagers.put(name, entityManager);
		this.connectionWrappers.stream().forEach((cw) -> {
			cw.callDatabaseCleaner(entityManager);
		});
	}
}
