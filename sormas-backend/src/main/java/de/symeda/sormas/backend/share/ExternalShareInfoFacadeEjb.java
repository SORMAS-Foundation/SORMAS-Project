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

package de.symeda.sormas.backend.share;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.share.ExternalShareInfoFacade;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "ExternalShareInfoFacade")
public class ExternalShareInfoFacadeEjb implements ExternalShareInfoFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ExternalShareInfoService service;

	@EJB
	private UserService userService;

	@Override
	public List<ExternalShareInfoDto> getIndexList(ExternalShareInfoCriteria criteria, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ExternalShareInfo> cq = cb.createQuery(ExternalShareInfo.class);
		final Root<ExternalShareInfo> shareInfo = cq.from(ExternalShareInfo.class);

		Predicate filter = service.createUserFilter(cb, cq, shareInfo);

		if (criteria != null) {
			Predicate criteriaFilter = service.buildCriteriaFilter(criteria, cb, shareInfo);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(shareInfo.get(ExternalShareInfo.CREATION_DATE)));

		List<ExternalShareInfo> shareInfoList = QueryHelper.getResultList(em, cq, first, max);

		Pseudonymizer<ExternalShareInfoDto> pseudonymizer = Pseudonymizer.getDefaultWithPlaceHolder(userService);
		return shareInfoList.stream().map(i -> convertToDto(i, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public boolean isSharedCase(String caseUuid) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ExternalShareStatus> cq = cb.createQuery(ExternalShareStatus.class);
		final Root<ExternalShareInfo> shareInfo = cq.from(ExternalShareInfo.class);
		Join<ExternalShareInfo, Case> caseJoin = shareInfo.join(ExternalShareInfo.CAZE, JoinType.LEFT);

		cq.select(shareInfo.get(ExternalShareInfo.STATUS));
		cq.where(cb.equal(caseJoin.get(Case.UUID), caseUuid));
		cq.orderBy(cb.desc(shareInfo.get(ExternalShareInfo.CREATION_DATE)));

		ExternalShareStatus externalShareStatus = null;
		try {
			externalShareStatus = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
		}

		return ExternalShareStatus.SHARED.equals(externalShareStatus);
	}

	@Override
	public boolean isSharedEvent(String eventUuid) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ExternalShareStatus> cq = cb.createQuery(ExternalShareStatus.class);
		final Root<ExternalShareInfo> shareInfo = cq.from(ExternalShareInfo.class);
		Join<ExternalShareInfo, Event> eventJoin = shareInfo.join(ExternalShareInfo.EVENT, JoinType.LEFT);

		cq.select(shareInfo.get(ExternalShareInfo.STATUS));
		cq.where(cb.equal(eventJoin.get(Event.UUID), eventUuid));
		cq.orderBy(cb.desc(shareInfo.get(ExternalShareInfo.CREATION_DATE)));

		ExternalShareStatus externalShareStatus = null;
		try {
			externalShareStatus = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
		}

		return ExternalShareStatus.SHARED.equals(externalShareStatus);
	}

	private ExternalShareInfoDto convertToDto(ExternalShareInfo source, Pseudonymizer<ExternalShareInfoDto> pseudonymizer) {
		ExternalShareInfoDto dto = toDto(source);

		boolean pseudonymized = pseudonymizer.pseudonymizeUser(source.getSender(), userService.getCurrentUser(), dto::setSender, dto);
		if (pseudonymized) {
			dto.setPseudonymized(true);
		}

		return dto;
	}

	private ExternalShareInfoDto toDto(ExternalShareInfo source) {
		ExternalShareInfoDto target = new ExternalShareInfoDto();

		DtoHelper.fillDto(target, source);

		target.setSender(UserFacadeEjb.toReferenceDto(source.getSender()));
		target.setStatus(source.getStatus());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ExternalShareInfoFacadeEjbLocal extends ExternalShareInfoFacadeEjb {
	}
}
