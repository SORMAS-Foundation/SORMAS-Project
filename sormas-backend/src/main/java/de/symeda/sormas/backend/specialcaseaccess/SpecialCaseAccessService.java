/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.specialcaseaccess;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class SpecialCaseAccessService extends BaseAdoService<SpecialCaseAccess> {

	public SpecialCaseAccessService() {
		super(SpecialCaseAccess.class);
	}

	public Collection<SpecialCaseAccess> getAllActiveByCase(CaseReferenceDto caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpecialCaseAccess> cq = cb.createQuery(getElementClass());
		Root<SpecialCaseAccess> from = cq.from(getElementClass());

		cq.where(
			cb.equal(from.join(SpecialCaseAccess.CAZE, JoinType.LEFT).get(Case.UUID), caze.getUuid()),
			cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date()));
		cq.orderBy(cb.desc(from.get(SpecialCaseAccess.END_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public boolean isAnyAssignedToUser(List<CaseReferenceDto> cases, UserReferenceDto user) {
		return IterableHelper.anyBatch(
			cases,
			ModelConstants.PARAMETER_LIMIT,
			batchedCases -> exists(
				(cb, from, cq) -> cb.and(
					from.join(SpecialCaseAccess.CAZE, JoinType.LEFT)
						.get(Case.UUID)
						.in(batchedCases.stream().map(CaseReferenceDto::getUuid).collect(Collectors.toList())),
					cb.equal(from.join(SpecialCaseAccess.ASSIGNED_TO, JoinType.LEFT).get(User.UUID), user.getUuid()),
					cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date()))));
	}

	public void deleteByCaseAndAssignee(CaseReferenceDto caze, UserReferenceDto assignedTo) {
		getByPredicate(
			(cb, from, cq) -> cb.and(
				cb.equal(from.join(SpecialCaseAccess.CAZE, JoinType.LEFT).get(Case.UUID), caze.getUuid()),
				cb.equal(from.join(SpecialCaseAccess.ASSIGNED_TO, JoinType.LEFT).get(User.UUID), assignedTo.getUuid())))
			.forEach(this::deletePermanent);
	}

	public boolean isGrantedToCurrentUser(CaseReferenceDto caze) {

		return exists(
			(cb, from, cq) -> cb.and(
				cb.equal(from.join(SpecialCaseAccess.CAZE, JoinType.LEFT).get(Case.UUID), caze.getUuid()),
				cb.equal(from.get(SpecialCaseAccess.ASSIGNED_TO), getCurrentUser()),
				cb.greaterThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new Date())));
	}
}
