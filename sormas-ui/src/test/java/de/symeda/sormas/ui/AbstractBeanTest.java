/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.ui;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;

import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import info.novatec.beantest.api.BaseBeanTest;

public class AbstractBeanTest extends BaseBeanTest {

	protected final TestDataCreator creator = new TestDataCreator();

	/**
	 * Resets mocks to their initial state so that mock configurations are not
	 * shared between tests.
	 */
	@Before
	public void init() {
		MockProducer.resetMocks();
		initH2Functions();
	}

	private void initH2Functions() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Query nativeQuery = em.createNativeQuery("CREATE ALIAS similarity FOR \"de.symeda.sormas.ui.H2Function.similarity\"");
		nativeQuery.executeUpdate();
		em.getTransaction().commit();
	}
	
	public PersonFacade getPersonFacade() {
		return getBean(PersonFacadeEjbLocal.class);
	}

	public CaseFacade getCaseFacade() {
		return getBean(CaseFacadeEjbLocal.class);
	}

	public EntityManager getEntityManager() {
		return getBean(EntityManagerWrapper.class).getEntityManager();
	}
}
