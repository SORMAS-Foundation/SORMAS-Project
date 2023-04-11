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

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SormasToSormasShareInfoFacade")
public class SormasToSormasShareInfoFacadeEjb implements SormasToSormasShareInfoFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Inject
	private SormasToSormasDiscoveryService sormasToSormasDiscoveryService;

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS,
		UserRight._SORMAS_TO_SORMAS_SHARE })
	public List<SormasToSormasShareInfoDto> getIndexList(SormasToSormasShareInfoCriteria criteria, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> root = cq.from(SormasToSormasShareInfo.class);

		Predicate filter = shareInfoService.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_SHARE)
	public SormasToSormasShareInfoDto getCaseShareInfoByOrganization(CaseReferenceDto caze, String organizationId) {
		SormasToSormasShareInfo shareInfo = shareInfoService.getByCaseAndOrganization(caze.getUuid(), organizationId);
		return toDto(shareInfo);
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_SHARE)
	public SormasToSormasShareInfoDto getContactShareInfoByOrganization(ContactReferenceDto contact, String organizationId) {
		SormasToSormasShareInfo shareInfo = shareInfoService.getByContactAndOrganization(contact.getUuid(), organizationId);
		return toDto(shareInfo);
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_SHARE)
	public SormasToSormasShareInfoDto getEventShareInfoByOrganization(EventReferenceDto event, String organizationId) {
		SormasToSormasShareInfo shareInfo = shareInfoService.getByEventAndOrganization(event.getUuid(), organizationId);
		return toDto(shareInfo);
	}

	@PermitAll
	public SormasToSormasShareInfoDto toDto(SormasToSormasShareInfo source) {
		if (source == null) {
			return null;
		}

		SormasToSormasShareInfoDto target = new SormasToSormasShareInfoDto();

		DtoHelper.fillDto(target, source);

		SormasServerDescriptor sormasServerDescriptor = sormasToSormasDiscoveryService.getSormasServerDescriptorById(source.getOrganizationId());
		if (sormasServerDescriptor == null) {
			sormasServerDescriptor = new SormasServerDescriptor(source.getOrganizationId(), source.getOrganizationId());
		}

		target.setTargetDescriptor(sormasServerDescriptor);

		ShareRequestInfo latestRequest = ShareInfoHelper.getLatestRequest(source.getRequests().stream()).orElseGet(ShareRequestInfo::new);

		target.setRequestStatus(latestRequest.getRequestStatus());
		target.setSender(latestRequest.getSender().toReference());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setWithAssociatedContacts(latestRequest.isWithAssociatedContacts());
		target.setWithSamples(latestRequest.isWithSamples());
		target.setWithEvenParticipants(latestRequest.isWithEventParticipants());
		target.setWithImmunizations(latestRequest.isWithImmunizations());
		target.setWithSurveillanceReports(latestRequest.isWithSurveillanceReports());
		target.setPseudonymizedPersonalData(latestRequest.isPseudonymizedPersonalData());
		target.setPseudonymizedSensitiveData(latestRequest.isPseudonymizedSensitiveData());
		target.setComment(latestRequest.getComment());
		target.setResponseComment(latestRequest.getResponseComment());

		return target;
	}

	@PermitAll
	public boolean hasAnyEntityReference(SormasToSormasShareInfo entity) {
		return entity.getCaze() != null
			|| entity.getContact() != null
			|| entity.getEvent() != null
			|| entity.getEventParticipant() != null
			|| entity.getImmunization() != null
			|| entity.getSample() != null;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasShareInfoFacadeEjbLocal extends SormasToSormasShareInfoFacadeEjb {

	}
}
