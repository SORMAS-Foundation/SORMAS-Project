/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.specialcaseaccess;

import java.util.Collection;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.BaseAdoService;

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
}
