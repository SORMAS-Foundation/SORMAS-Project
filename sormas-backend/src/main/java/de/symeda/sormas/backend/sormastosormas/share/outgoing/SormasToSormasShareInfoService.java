/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.share.outgoing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class SormasToSormasShareInfoService extends AdoServiceWithUserFilterAndJurisdiction<SormasToSormasShareInfo> {

	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolGatewayFacade;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

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

		if (criteria.getImmunization() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.IMMUNIZATION, JoinType.LEFT).get(Immunization.UUID),
					criteria.getImmunization().getUuid()));
		}

		if (criteria.getSurveillanceReport() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(
					from.join(SormasToSormasShareInfo.SURVEILLANCE_REPORT, JoinType.LEFT).get(SurveillanceReport.UUID),
					criteria.getSurveillanceReport().getUuid()));
		}

		return filter;
	}

	public boolean isCaseOwnershipHandedOver(Case caze) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.CAZE, caze);
	}

	public boolean isEventOwnershipHandedOver(Event event) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.EVENT, event);
	}

	public boolean isEventParticipantOwnershipHandedOver(EventParticipant eventParticipant) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.EVENT_PARTICIPANT, eventParticipant);
	}

	public boolean isContactOwnershipHandedOver(Contact contact) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.CONTACT, contact);
	}

	public boolean isSamlpeOwnershipHandedOver(Sample sample) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.SAMPLE, sample);
	}

	public boolean isImmunizationsOwnershipHandedOver(Immunization immunization) {
		return isOwnerShipHandedOver(SormasToSormasShareInfo.IMMUNIZATION, immunization);
	}

	private boolean isOwnerShipHandedOver(String associatedObjectField, AbstractDomainObject associatedObject) {
		return exists(
			(cb, root, cq) -> cb.and(
				cb.equal(root.get(associatedObjectField), associatedObject),
				getOwnershipHandedOverFilter(cq, cb, root, ShareRequestStatus.ACCEPTED)));
	}

	private Predicate getOwnershipHandedOverFilter(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		Root<SormasToSormasShareInfo> root,
		ShareRequestStatus requestStatus) {
		Subquery<Number> latestRequestDateQuery = cq.subquery(Number.class);
		Root<ShareRequestInfo> shareRequestInfoRoot = latestRequestDateQuery.from(ShareRequestInfo.class);
		latestRequestDateQuery.select(cb.max(shareRequestInfoRoot.get(ShareRequestInfo.CREATION_DATE)));
		latestRequestDateQuery.where(
			cb.equal(
				shareRequestInfoRoot.join(ShareRequestInfo.SHARES, JoinType.LEFT).get(SormasToSormasShareInfo.ID),
				root.get(SormasToSormasShareInfo.ID)));

		Join<Object, Object> requests = root.join(SormasToSormasShareInfo.REQUESTS, JoinType.LEFT);
		return cb.and(
			cb.equal(requests.get(ShareRequestInfo.REQUEST_STATUS), requestStatus),
			cb.equal(requests.get(ShareRequestInfo.CREATION_DATE), latestRequestDateQuery),
			cb.isTrue(root.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER)));
	}

	public SormasToSormasShareInfo getByCaseAndOrganization(String caseUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.CAZE, caseUuid, organizationId);
	}

	public SormasToSormasShareInfo getByContactAndOrganization(String contactUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.CONTACT, contactUuid, organizationId);
	}

	public SormasToSormasShareInfo getByEventAndOrganization(String eventUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.EVENT, eventUuid, organizationId);
	}

	public SormasToSormasShareInfo getByEventParticipantAndOrganization(String eventParticipantUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.EVENT_PARTICIPANT, eventParticipantUuid, organizationId);
	}

	public SormasToSormasShareInfo getBySampleAndOrganization(String sampleUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.SAMPLE, sampleUuid, organizationId);
	}

	public SormasToSormasShareInfo getByImmunizationAndOrganization(String immunizationUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.IMMUNIZATION, immunizationUuid, organizationId);
	}

	public SormasToSormasShareInfo getBySurveillanceReportAndOrganization(String surveillanceUuid, String organizationId) {
		return getByOrganization(SormasToSormasShareInfo.SURVEILLANCE_REPORT, surveillanceUuid, organizationId);
	}

	public SormasToSormasShareInfo getByOrganization(String associatedObjectField, String associatedObjectUuid, String organizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(
			cb.equal(from.join(associatedObjectField, JoinType.LEFT).get(AbstractDomainObject.UUID), associatedObjectUuid),
			cb.equal(from.get(SormasToSormasShareInfo.ORGANIZATION_ID), organizationId));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public List<SormasToSormasShareInfo> getByAssociatedEntity(String associatedObjectField, String associatedObjectUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> from = cq.from(SormasToSormasShareInfo.class);

		cq.where(cb.equal(from.join(associatedObjectField, JoinType.LEFT).get(AbstractDomainObject.UUID), associatedObjectUuid));

		TypedQuery<SormasToSormasShareInfo> q = em.createQuery(cq);

		return q.getResultList();
	}

	public boolean hasPendingRequest(String associatedObjectField, List<String> associatedObjectUuids) {

		return IterableHelper.anyBatch(
			associatedObjectUuids,
			ModelConstants.PARAMETER_LIMIT,
			(batch) -> exists(
				((cb, root, cq) -> cb.and(
					root.join(associatedObjectField, JoinType.LEFT).get(AbstractDomainObject.UUID).in(batch),
					cb.equal(
						root.join(SormasToSormasShareInfo.REQUESTS, JoinType.LEFT).get(ShareRequestInfo.REQUEST_STATUS),
						ShareRequestStatus.PENDING)))));
	}

	public List<String> getCaseUuidsWithPendingOwnershipHandOver(List<Case> cases) {
		return getUuidsWithPendingOwnershipHandOver(SormasToSormasShareInfo.CAZE, cases);
	}

	public List<String> getContactUuidsWithPendingOwnershipHandOver(List<Contact> contacts) {
		return getUuidsWithPendingOwnershipHandOver(SormasToSormasShareInfo.CONTACT, contacts);
	}

	public List<String> getEventUuidsWithPendingOwnershipHandOver(List<Event> events) {
		return getUuidsWithPendingOwnershipHandOver(SormasToSormasShareInfo.EVENT, events);
	}

	public void handleOwnershipChangeInExternalSurvTool(ShareRequestInfo requestInfo) throws ExternalSurveillanceToolException {
		List<Case> cases =
			requestInfo.getShares().stream().map(SormasToSormasShareInfo::getCaze).filter(Objects::nonNull).collect(Collectors.toList());
		List<Event> events =
			requestInfo.getShares().stream().map(SormasToSormasShareInfo::getEvent).filter(Objects::nonNull).collect(Collectors.toList());

		handleOwnershipChangeInExternalSurvTool(requestInfo.getShares().get(0).isOwnershipHandedOver(), cases, events);
	}

	private <ADO extends AbstractDomainObject> List<String> getUuidsWithPendingOwnershipHandOver(
		String associatedObjectField,
		List<ADO> associatedObjects) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();

		final CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<SormasToSormasShareInfo> root = query.from(SormasToSormasShareInfo.class);

		Join<SormasToSormasShareInfo, AbstractDomainObject> associatedObjectJoin = root.join(associatedObjectField, JoinType.LEFT);

		query.select(associatedObjectJoin.get(AbstractDomainObject.UUID));
		query.where(associatedObjectJoin.in(associatedObjects), cb.and(getOwnershipHandedOverFilter(query, cb, root, ShareRequestStatus.PENDING)));

		return em.createQuery(query).getResultList();
	}

	private void handleOwnershipChangeInExternalSurvTool(boolean isOwnershipHandedOver, List<Case> cases, List<Event> events)
		throws ExternalSurveillanceToolException {
		if (externalSurveillanceToolGatewayFacade.isFeatureEnabled() && isOwnershipHandedOver) {
			List<Case> sharedCases = cases.stream().filter(c -> !c.getExternalShares().isEmpty()).collect(Collectors.toList());
			if (sharedCases.size() > 0) {
				externalSurveillanceToolGatewayFacade.deleteCasesInternal(caseFacade.toDtos(sharedCases.stream()));
			}

			List<Event> sharedEvents = events.stream().filter(e -> !e.getExternalShares().isEmpty()).collect(Collectors.toList());
			if (sharedEvents.size() > 0) {
				externalSurveillanceToolGatewayFacade.deleteEventsInternal(eventFacade.toDtos(sharedEvents.stream()));
			}
		}
	}
}
