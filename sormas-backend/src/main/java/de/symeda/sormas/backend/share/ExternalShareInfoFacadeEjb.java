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
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.share.ExternalShareInfoFacade;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

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

		List<ExternalShareInfo> shareInfoList;
		if (first != null && max != null) {
			shareInfoList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			shareInfoList = em.createQuery(cq).getResultList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		return shareInfoList.stream().map(i -> convertToDto(i, pseudonymizer)).collect(Collectors.toList());
	}

	private ExternalShareInfoDto convertToDto(ExternalShareInfo source, Pseudonymizer pseudonymizer) {
		ExternalShareInfoDto dto = toDto(source);

		pseudonymizer.pseudonymizeUser(source.getSender(), userService.getCurrentUser(), dto::setSender);

		return dto;
	}

	private ExternalShareInfoDto toDto(ExternalShareInfo source) {
		ExternalShareInfoDto target = new ExternalShareInfoDto();

		DtoHelper.fillDto(target, source);

		target.setSender(source.getSender().toReference());
		target.setStatus(source.getStatus());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ExternalShareInfoFacadeEjbLocal extends ExternalShareInfoFacadeEjb {
	}
}
