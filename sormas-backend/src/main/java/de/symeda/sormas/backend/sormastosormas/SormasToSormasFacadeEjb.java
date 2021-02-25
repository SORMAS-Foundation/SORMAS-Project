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

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SormasToSormasFacade")
public class SormasToSormasFacadeEjb implements SormasToSormasFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private UserService userService;
	@EJB
	private ServerAccessDataService serverAccessDataService;
	@EJB
	private SormasToSormasFacadeHelper sormasToSormasFacadeHelper;

	@Override
	public List<ServerAccessDataReferenceDto> getAvailableOrganizations() {
		return serverAccessDataService.getOrganizationList().stream().map(OrganizationServerAccessData::toReference).collect(Collectors.toList());
	}

	@Override
	public List<SormasToSormasShareInfoDto> getShareInfoIndexList(SormasToSormasShareInfoCriteria criteria, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> root = cq.from(SormasToSormasShareInfo.class);

		Predicate filter = shareInfoService.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		List<SormasToSormasShareInfo> resultList;
		if (first != null && max != null) {
			resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			resultList = em.createQuery(cq).getResultList();
		}

		return resultList.stream().map(this::toSormasToSormasShareInfoDto).collect(Collectors.toList());
	}

	@Override
	public boolean isFeatureEnabled() {
		return userService.hasRight(UserRight.SORMAS_TO_SORMAS_SHARE) && !serverAccessDataService.getOrganizationList().isEmpty();
	}

	@Override
	public ServerAccessDataReferenceDto getOrganizationRef(String id) {
		return sormasToSormasFacadeHelper.getOrganizationServerAccessData(id).map(OrganizationServerAccessData::toReference).orElseGet(null);
	}

	public SormasToSormasShareInfoDto toSormasToSormasShareInfoDto(SormasToSormasShareInfo source) {
		SormasToSormasShareInfoDto target = new SormasToSormasShareInfoDto();

		DtoHelper.fillDto(target, source);

		if (source.getCaze() != null) {
			target.setCaze(source.getCaze().toReference());
		}

		if (source.getContact() != null) {
			target.setContact(source.getContact().toReference());
		}

		if (source.getSample() != null) {
			target.setSample(source.getSample().toReference());
		}

		OrganizationServerAccessData serverAccessData = sormasToSormasFacadeHelper.getOrganizationServerAccessData(source.getOrganizationId())
			.orElseGet(() -> new OrganizationServerAccessData(source.getOrganizationId(), source.getOrganizationId()));
		target.setTarget(serverAccessData.toReference());

		target.setSender(source.getSender().toReference());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setWithAssociatedContacts(source.isWithAssociatedContacts());
		target.setWithSamples(source.isWithSamples());
		target.setWithEvenParticipants(source.isWithEventParticipants());
		target.setPseudonymizedPersonalData(source.isPseudonymizedPersonalData());
		target.setPseudonymizedSensitiveData(source.isPseudonymizedSensitiveData());
		target.setComment(source.getComment());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}
}
