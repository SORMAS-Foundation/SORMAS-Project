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
package de.symeda.sormas.backend.hospitalization;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PreviousHospitalizationService extends AbstractAdoService<PreviousHospitalization> {

	public PreviousHospitalizationService() {
		super(PreviousHospitalization.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, 
			From<PreviousHospitalization, PreviousHospitalization> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}

	public PreviousHospitalization buildPreviousHospitalizationFromHospitalization(Case caze) {
		PreviousHospitalization previousHospitalization = new PreviousHospitalization();
		previousHospitalization.setUuid(DataHelper.createUuid());

		Hospitalization hospitalization = caze.getHospitalization();

		if (hospitalization.getAdmissionDate() != null) {
			previousHospitalization.setAdmissionDate(hospitalization.getAdmissionDate());
		} else {
			previousHospitalization.setAdmissionDate(caze.getReportDate());
		}

		if (hospitalization.getDischargeDate() != null) {
			previousHospitalization.setDischargeDate(hospitalization.getDischargeDate());
		} else {
			previousHospitalization.setDischargeDate(new Date());
		}

		previousHospitalization.setRegion(caze.getRegion());
		previousHospitalization.setDistrict(caze.getDistrict());
		previousHospitalization.setCommunity(caze.getCommunity());
		previousHospitalization.setHealthFacility(caze.getHealthFacility());
		previousHospitalization.setHospitalization(caze.getHospitalization());
		previousHospitalization.setIsolated(hospitalization.getIsolated());

		return previousHospitalization;
	}

	public PreviousHospitalization getInitialHospitalization(long hospitalizationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PreviousHospitalization> cq = cb.createQuery(PreviousHospitalization.class);
		Root<PreviousHospitalization> prevHosp = cq.from(getElementClass());
		cq.where(cb.equal(prevHosp.get(PreviousHospitalization.HOSPITALIZATION).get(Hospitalization.ID), hospitalizationId));
		cq.orderBy(cb.asc(prevHosp.get(PreviousHospitalization.ADMISSION_DATE)));
		
		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
