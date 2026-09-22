/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
 */
package de.symeda.sormas.backend.event.sevenoneseven;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.event.Event;

@Stateless
@LocalBean
public class Event717AssessmentService extends BaseAdoService<Event717Assessment> {

	public Event717AssessmentService() {
		super(Event717Assessment.class);
	}

	public Event717Assessment getByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event717Assessment> cq = cb.createQuery(Event717Assessment.class);
		Root<Event717Assessment> from = cq.from(Event717Assessment.class);
		cq.where(cb.equal(from.get(Event717Assessment.EVENT), event));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public Event717Assessment getByEventUuid(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event717Assessment> cq = cb.createQuery(Event717Assessment.class);
		Root<Event717Assessment> from = cq.from(Event717Assessment.class);
		Join<Event717Assessment, Event> eventJoin = from.join(Event717Assessment.EVENT);
		cq.where(cb.equal(eventJoin.get(AbstractDomainObject.UUID), eventUuid));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public void deletePermanentByEvent(Event event) {

		Event717Assessment assessment = getByEvent(event);
		if (assessment != null) {
			deletePermanent(assessment);
		}
	}

	@Override
	public boolean deletePermanent(Event717Assessment assessment) {

		// Corrective actions may reference bottlenecks of the same assessment; unlink them first so the cascaded delete
		// does not depend on the order in which the child rows are removed
		assessment.getCorrectiveActions().forEach(action -> action.setBottleneck(null));
		em.flush();

		return super.deletePermanent(assessment);
	}
}
