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
 */

package de.symeda.sormas.backend.sormastosormas.shareinfo;

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
import de.symeda.sormas.backend.common.AbstractDomainObject;
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
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.CASES, JoinType.LEFT).join(ShareInfoCase.CAZE, JoinType.LEFT).get(Case.UUID),
					criteria.getCaze().getUuid()));
		}

		if (criteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.CONTACTS, JoinType.LEFT).join(ShareInfoContact.CONTACT, JoinType.LEFT).get(Contact.UUID),
					criteria.getContact().getUuid()));
		}

		if (criteria.getSample() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.SAMPLES, JoinType.LEFT).join(ShareInfoSample.SAMPLE, JoinType.LEFT).get(Sample.UUID),
					criteria.getSample().getUuid()));
		}

		if (criteria.getEvent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.EVENTS, JoinType.LEFT).join(ShareInfoEvent.EVENT, JoinType.LEFT).get(Event.UUID),
					criteria.getEvent().getUuid()));
		}

		if (criteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.EVENT_PARTICIPANTS, JoinType.LEFT)
						.join(ShareInfoEventParticipant.EVENT_PARTICIPANT, JoinType.LEFT)
						.get(EventParticipant.UUID),
					criteria.getEventParticipant().getUuid()));
		}

		return filter;
	}

	public boolean isCaseOwnershipHandedOver(Case caze) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.CASES, ShareInfoCase.CAZE, caze);
	}

	public boolean isEventOwnershipHandedOver(Event event) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.EVENTS, ShareInfoEvent.EVENT, event);
	}

	public boolean isEventOwnershipHandedOver(EventParticipant eventParticipant) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.EVENT_PARTICIPANTS, ShareInfoEventParticipant.EVENT_PARTICIPANT, eventParticipant);
	}

	public boolean isContactOwnershipHandedOver(Contact contact) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.CONTACTS, ShareInfoContact.CONTACT, contact);
	}

	public boolean isSamlpeOwnershipHandedOver(Sample sample) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.SAMPLES, ShareInfoSample.SAMPLE, sample);
	}

	private boolean isOwnerShipHandedOver(String associatedObjectField, String associatedObjectName, AbstractDomainObject associatedObject) {
		return exists(
			(cb, root) -> cb.and(
				cb.equal(root.join(associatedObjectField).get(associatedObjectName), associatedObject),
				cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
	}

	public SormasToSormasShareInfo getByCaseAndOrganization(String caseUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.CASES, ShareInfoCase.CAZE, caseUuid, organizationId);
	}

	public SormasToSormasShareInfo getByContactAndOrganization(String contactUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.CONTACTS, ShareInfoContact.CONTACT, contactUuid, organizationId);
	}

	public SormasToSormasShareInfo getByEventAndOrganization(String eventUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.EVENTS, ShareInfoEvent.EVENT, eventUuid, organizationId);
	}

	public SormasToSormasShareInfo getByEventParticipantAndOrganization(String eventParticipantUuid, String organizationId) {
		return getByOrganization(
			SormasToSormasShareInfo.EVENT_PARTICIPANTS,
			ShareInfoEventParticipant.EVENT_PARTICIPANT,
			eventParticipantUuid,
			organizationId);
	}

	public SormasToSormasShareInfo getBySampleAndOrganization(String sampleUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.SAMPLES, ShareInfoSample.SAMPLE, sampleUuid, organizationId);
	}

	public SormasToSormasShareInfo getByOrganization(
		String associatedObjectField,
		String associatedObjectName,
		String associatedObjectUuid,
		String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.join(associatedObjectField).join(associatedObjectName, JoinType.LEFT).get(AbstractDomainObject.UUID), associatedObjectUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public SormasToSormasShareInfo getByRequestUuid(String requestUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(cb.equal(from.get(SormasToSormasShareInfo.REQUEST_UUID), requestUuid));

		return em.createQuery(cq).getSingleResult();
	}
}
