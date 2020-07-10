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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.action;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ActionService extends AbstractAdoService<Action> {

	@EJB
	private EventService eventService;

	public ActionService() {
		super(Action.class);
	}

	public List<Action> getAllByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Action, Action> actionPath) {

		// National users can access all actions in the system
		User currentUser = getCurrentUser();
		if (currentUser.hasAnyUserRole(UserRole.NATIONAL_USER, UserRole.NATIONAL_CLINICIAN, UserRole.NATIONAL_OBSERVER, UserRole.REST_USER)) {
			return null;
		}

		// whoever created the action is allowed to access it
		Predicate filter = cb.equal(actionPath.join(Action.CREATOR_USER, JoinType.LEFT), currentUser);

		Predicate eventFilter = eventService.createUserFilter(cb, cq, actionPath.join(Action.EVENT, JoinType.LEFT));
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}

		return filter;
	}

}
