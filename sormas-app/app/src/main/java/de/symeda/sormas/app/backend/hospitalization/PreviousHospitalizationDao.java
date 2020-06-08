/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.hospitalization;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDao extends AbstractAdoDao<PreviousHospitalization> {

	public PreviousHospitalizationDao(Dao<PreviousHospitalization, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<PreviousHospitalization> getAdoClass() {
		return PreviousHospitalization.class;
	}

	@Override
	public String getTableName() {
		return PreviousHospitalization.TABLE_NAME;
	}

	public List<PreviousHospitalization> getByHospitalization(Hospitalization hospitalization) {
		if (hospitalization.isSnapshot()) {
			return querySnapshotsForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
		}
		return queryForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
	}

	public PreviousHospitalization buildPreviousHospitalizationFromHospitalization(Case caze, Case oldCase) {
		PreviousHospitalization previousHospitalization = super.build();
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

		previousHospitalization.setRegion(oldCase.getRegion());
		previousHospitalization.setDistrict(oldCase.getDistrict());
		previousHospitalization.setCommunity(oldCase.getCommunity());
		previousHospitalization.setHealthFacility(oldCase.getHealthFacility());
		previousHospitalization.setHealthFacilityDetails(oldCase.getHealthFacilityDetails());
		previousHospitalization.setHospitalization(caze.getHospitalization());
		previousHospitalization.setIsolated(hospitalization.getIsolated());

		return previousHospitalization;
	}
}
