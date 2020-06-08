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

public class PrescriptionDao extends AbstractAdoDao<Prescription> {

	public PrescriptionDao(Dao<Prescription, Long> innerDao) {
		super(innerDao);
	}

	public Prescription build(Case caze) {
		Prescription prescription = super.build();
		prescription.setTherapy(caze.getTherapy());
		prescription.setPrescriptionDate(new Date());
		return prescription;
	}

	@Override
	protected Class<Prescription> getAdoClass() {
		return Prescription.class;
	}

	@Override
	public String getTableName() {
		return Prescription.TABLE_NAME;
	}

	public List<Prescription> findBy(PrescriptionCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).orderBy(Prescription.PRESCRIPTION_DATE, true).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform findBy on Prescription");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(PrescriptionCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Prescription");
			throw new RuntimeException(e);
		}
	}

	public List<Prescription> queryByCriteria(PrescriptionCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Prescription.PRESCRIPTION_DATE, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Prescription");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Prescription, Long> buildQueryBuilder(PrescriptionCriteria criteria) throws SQLException {
		QueryBuilder<Prescription, Long> queryBuilder = queryBuilder();
		Where<Prescription, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		if (criteria.getTherapy() != null) {
			where.and().eq(Prescription.THERAPY + "_id", criteria.getTherapy());
		}

		queryBuilder.setWhere(where);
		return queryBuilder;
	}
}
