/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.backend.epidata;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EpiDataTravelService extends AbstractAdoService<EpiDataTravel> {

	public EpiDataTravelService() {
		super(EpiDataTravel.class);
	}

	public List<EpiDataTravel> getAllByEpiDataId(long epiDataId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EpiDataTravel> cq = cb.createQuery(EpiDataTravel.class);
		Root<EpiDataTravel> root = cq.from(getElementClass());
		cq.where(cb.equal(root.get(EpiDataTravel.EPI_DATA).get(EpiData.ID), epiDataId));
		cq.select(root);
		List<EpiDataTravel> result = em.createQuery(cq).getResultList();
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EpiDataTravel, EpiDataTravel> from,
			User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
