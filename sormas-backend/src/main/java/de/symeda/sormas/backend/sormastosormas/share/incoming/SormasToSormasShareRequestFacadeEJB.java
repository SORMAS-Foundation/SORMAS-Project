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

package de.symeda.sormas.backend.sormastosormas.share.incoming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestDetailsDto;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestIndexDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SormasToSormasShareRequestFacade")
public class SormasToSormasShareRequestFacadeEJB implements SormasToSormasShareRequestFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SormasToSormasShareRequestService shareRequestService;

	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Inject
	private SormasToSormasDiscoveryService sormasToSormasDiscoveryService;

	@EJB
	private SormasToSormasOriginInfoService sormasToSormasOriginInfoService;

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS,
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public SormasToSormasShareRequestDto saveShareRequest(@Valid SormasToSormasShareRequestDto dto) {
		SormasToSormasShareRequest existingSormasToSormasShareRequest = shareRequestService.getByUuid(dto.getUuid());
		SormasToSormasShareRequest request = fillOrBuildEntity(dto, existingSormasToSormasShareRequest, true);

		shareRequestService.ensurePersisted(request);

		return toDto(request);
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public SormasToSormasShareRequestDto getShareRequestByUuid(String uuid) {
		SormasToSormasShareRequest request = shareRequestService.getByUuid(uuid);

		return toDto(request);
	}

	public static SormasToSormasShareRequestDto toDto(SormasToSormasShareRequest source) {
		if (source == null) {
			return null;
		}
		SormasToSormasShareRequestDto target = new SormasToSormasShareRequestDto();
		DtoHelper.fillDto(target, source);

		target.setDataType(source.getDataType());
		target.setStatus(source.getStatus());
		target.setOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getOriginInfo()));
		target.setCases(source.getCasesList());
		target.setContacts(source.getContactsList());
		target.setEvents(source.getEventsList());
		target.setEventParticipants(source.getEventParticipantsList());
		target.setResponseComment(source.getResponseComment());

		return target;
	}

	public static ShareRequestDetailsDto toDetailsDto(SormasToSormasShareRequest source) {
		if (source == null) {
			return null;
		}
		ShareRequestDetailsDto target = new ShareRequestDetailsDto();
		DtoHelper.fillDto(target, source);

		target.setDataType(source.getDataType());
		target.setStatus(source.getStatus());
		target.setCases(source.getCasesList());
		target.setContacts(source.getContactsList());
		target.setEvents(source.getEventsList());
		target.setEventParticipants(source.getEventParticipantsList());

		return target;
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public List<ShareRequestIndexDto> getIndexList(ShareRequestCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ShareRequestIndexDto> cq = cb.createQuery(ShareRequestIndexDto.class);
		Root<SormasToSormasShareRequest> requestRoot = cq.from(SormasToSormasShareRequest.class);
		Path<SormasToSormasOriginInfo> originInfo = requestRoot.get(SormasToSormasShareRequest.ORIGIN_INFO);

		cq.multiselect(
			requestRoot.get(SormasToSormasShareRequest.UUID),
			requestRoot.get(SormasToSormasShareRequest.CREATION_DATE),
			requestRoot.get(SormasToSormasShareRequest.DATA_TYPE),
			requestRoot.get(SormasToSormasShareRequest.STATUS),
			originInfo.get(SormasToSormasOriginInfo.ORGANIZATION_ID),
			originInfo.get(SormasToSormasOriginInfo.SENDER_NAME),
			originInfo.get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER),
			originInfo.get(SormasToSormasOriginInfo.COMMENT));

		Predicate filter = null;
		if (criteria != null) {
			filter = shareRequestService.buildCriteriaFilter(criteria, cb, requestRoot);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<Order> order = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SormasToSormasShareRequest.UUID:
				case SormasToSormasShareRequest.CREATION_DATE:
				case SormasToSormasShareRequest.DATA_TYPE:
				case SormasToSormasShareRequest.STATUS:
					expression = requestRoot.get(sortProperty.propertyName);
					break;
				case SormasToSormasOriginInfo.ORGANIZATION_ID:
				case SormasToSormasOriginInfo.SENDER_NAME:
				case SormasToSormasOriginInfo.COMMENT:
					expression = cb.lower(originInfo.get(sortProperty.propertyName));
					break;
				case SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER:
					expression = originInfo.get(sortProperty.propertyName);
					break;
				case ShareRequestIndexDto.ORGANIZATION_NAME:
					expression = cb.lower(originInfo.get(SormasToSormasOriginInfo.ORGANIZATION_ID));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}

		cq.orderBy(order);

		List<ShareRequestIndexDto> requests = QueryHelper.getResultList(em, cq, first, max);

		if (!requests.isEmpty()) {
			Map<String, SormasServerDescriptor> serverDescriptorMap = sormasToSormasDiscoveryService.getAllAvailableServers()
				.stream()
				.collect(Collectors.toMap(SormasServerDescriptor::getId, Function.identity()));

			requests.forEach(request -> {
				String organizationId = request.getOrganizationId();
				SormasServerDescriptor serverDescriptor = serverDescriptorMap.get(organizationId);

				request.setOrganizationName(serverDescriptor != null ? serverDescriptor.getName() : organizationId);
			});
		}

		return requests;
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public long count(ShareRequestCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<SormasToSormasShareRequest> requestRoot = cq.from(SormasToSormasShareRequest.class);

		Predicate filter = null;
		if (criteria != null) {
			filter = shareRequestService.buildCriteriaFilter(criteria, cb, requestRoot);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(requestRoot));

		return em.createQuery(cq).getSingleResult();
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public Page<ShareRequestIndexDto> getIndexPage(ShareRequestCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<ShareRequestIndexDto> shareRequestIndexList = getIndexList(criteria, first, max, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(shareRequestIndexList, first, max, totalElementCount);
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public List<SormasToSormasShareRequestDto> getShareRequestsForCase(CaseReferenceDto caze) {
		return shareRequestService.getShareRequestsForCase(caze)
			.stream()
			.map(SormasToSormasShareRequestFacadeEJB::toDto)
			.collect(Collectors.toList());
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE })
	public ShareRequestDetailsDto getShareRequestDetails(String uuid) {
		SormasToSormasShareRequest request = shareRequestService.getByUuid(uuid);

		return toDetailsDto(request);
	}

	private SormasToSormasShareRequest fillOrBuildEntity(
		@NotNull SormasToSormasShareRequestDto source,
		SormasToSormasShareRequest target,
		boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SormasToSormasShareRequest::new, checkChangeDate);

		target.setDataType(source.getDataType());
		target.setStatus(source.getStatus());

		// #10679: originInfo should be reference type
		target.setOriginInfo(
			originInfoFacade.fillOrBuildEntity(
				source.getOriginInfo(),
				sormasToSormasOriginInfoService.getByUuid(source.getOriginInfo().getUuid()),
				checkChangeDate));
		target.setCasesList(source.getCases());
		target.setContactsList(source.getContacts());
		target.setEventsList(source.getEvents());
		target.setEventParticipantsList(source.getEventParticipants());
		target.setResponseComment(source.getResponseComment());

		return target;
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public List<SormasToSormasShareRequestDto> getShareRequestsByUuids(List<String> uuids) {
		return shareRequestService.getByUuids(uuids).stream().map(SormasToSormasShareRequestFacadeEJB::toDto).collect(Collectors.toList());
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasShareRequestFacadeEJBLocal extends SormasToSormasShareRequestFacadeEJB {

	}
}
