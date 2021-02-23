/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.sample.Sample;

@Stateless
@LocalBean
public class SormasToSormasShareInfoService extends AdoServiceWithUserFilter<SormasToSormasShareInfo> {

	public SormasToSormasShareInfoService() {
		super(SormasToSormasShareInfo.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, SormasToSormasShareInfo> from) {
		// no user filter needed right now
		return null;
	}

	public Predicate buildCriteriaFilter(SormasToSormasShareInfoCriteria criteria, CriteriaBuilder cb, Root<SormasToSormasShareInfo> from) {
		Predicate filter = null;

		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(SormasToSormasShareInfo.CAZE, JoinType.LEFT).get(Case.UUID), criteria.getCaze().getUuid()));
		}

		if (criteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(SormasToSormasShareInfo.CONTACT, JoinType.LEFT).get(Contact.UUID), criteria.getContact().getUuid()));
		}

		if (criteria.getSample() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(SormasToSormasShareInfo.SAMPLE, JoinType.LEFT).get(Sample.UUID), criteria.getSample().getUuid()));
		}

		if (criteria.getEvent() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(SormasToSormasShareInfo.EVENT, JoinType.LEFT).get(Event.UUID), criteria.getEvent().getUuid()));
		}

		if (criteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.EVENT_PARTICIPANT, JoinType.LEFT).get(EventParticipant.UUID),
					criteria.getEventParticipant().getUuid()));
		}

		return filter;
	}

	public boolean isCaseOwnershipHandedOver(Case caze) {
		return exists(
			(cb, root) -> cb
				.and(cb.equal(root.get(SormasToSormasShareInfo.CAZE), caze), cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public boolean isEventOwnershipHandedOver(Event event) {
		return exists(
			(cb, root) -> cb
				.and(cb.equal(root.get(SormasToSormasShareInfo.EVENT), event), cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public boolean isEventOwnershipHandedOver(EventParticipant eventParticipant) {
		return exists(
			(cb, root) -> cb.and(
				cb.equal(root.get(SormasToSormasShareInfo.EVENT_PARTICIPANT), eventParticipant),
				cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public boolean isContactOwnershipHandedOver(Contact contact) {
		return exists(
			(cb, root) -> cb.and(
				cb.equal(root.get(SormasToSormasShareInfo.CONTACT), contact),
				cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public boolean isSamlpeOwnershipHandedOver(Sample sample) {
		return exists(
			(cb, root) -> cb
				.and(cb.equal(root.get(SormasToSormasShareInfo.SAMPLE), sample), cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public SormasToSormasShareInfo getByCaseAndOrganization(String caseUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.get(SormasToSormasShareInfo.CAZE).get(Case.UUID), caseUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public SormasToSormasShareInfo getByContactAndOrganization(String contactUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.get(SormasToSormasShareInfo.CONTACT).get(Contact.UUID), contactUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public SormasToSormasShareInfo getByEventAndOrganization(String contactUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.get(SormasToSormasShareInfo.EVENT).get(Contact.UUID), contactUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public SormasToSormasShareInfo getByEventParticipantAndOrganization(String eventParticipantUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.get(SormasToSormasShareInfo.EVENT_PARTICIPANT).get(Contact.UUID), eventParticipantUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public SormasToSormasShareInfo getBySampleAndOrganization(String sampleUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.get(SormasToSormasShareInfo.SAMPLE).get(Sample.UUID), sampleUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}
}
