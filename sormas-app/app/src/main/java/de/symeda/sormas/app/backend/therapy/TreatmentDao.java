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

package de.symeda.sormas.app.backend.therapy;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class TreatmentDao extends AbstractAdoDao<Treatment> {

	public TreatmentDao(Dao<Treatment, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Treatment> getAdoClass() {
		return Treatment.class;
	}

	@Override
	public String getTableName() {
		return Treatment.TABLE_NAME;
	}

	public Treatment build(Case caze) {
		Treatment treatment = super.build();
		treatment.setTherapy(caze.getTherapy());
		treatment.setTreatmentDateTime(new Date());
		return treatment;
	}

	public Treatment build(Prescription prescription) {
		Treatment treatment = super.build();
		treatment.setTherapy(prescription.getTherapy());
		treatment.setTreatmentDateTime(new Date());
		treatment.setTreatmentType(prescription.getPrescriptionType());
		treatment.setTreatmentDetails(prescription.getPrescriptionDetails());
		treatment.setTypeOfDrug(prescription.getTypeOfDrug());
		treatment.setDose(prescription.getDose());
		treatment.setRoute(prescription.getRoute());
		treatment.setRouteDetails(prescription.getRouteDetails());
		treatment.setPrescription(prescription);
		return treatment;
	}

	public List<Treatment> findBy(TreatmentCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).orderBy(Treatment.TREATMENT_DATE_TIME, true).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform findBy on Treatment");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(TreatmentCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Treatment");
			throw new RuntimeException(e);
		}
	}

	public List<Treatment> queryByCriteria(TreatmentCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Treatment.TREATMENT_DATE_TIME, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Treatment");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Treatment, Long> buildQueryBuilder(TreatmentCriteria criteria) throws SQLException {
		QueryBuilder<Treatment, Long> queryBuilder = queryBuilder();
		Where<Treatment, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		if (criteria.getTherapy() != null) {
			where.and().eq(Treatment.THERAPY + "_id", criteria.getTherapy());
		}

		queryBuilder.setWhere(where);
		return queryBuilder;
	}
}
