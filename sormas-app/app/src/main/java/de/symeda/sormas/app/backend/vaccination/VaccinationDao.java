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

package de.symeda.sormas.app.backend.vaccination;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;

public class VaccinationDao extends AbstractAdoDao<Vaccination> {

	public VaccinationDao(Dao<Vaccination, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Vaccination> getAdoClass() {
		return Vaccination.class;
	}

	@Override
	public String getTableName() {
		return Vaccination.TABLE_NAME;
	}

	public List<Vaccination> getByImmunization(Immunization immunization) {
		if (immunization.isSnapshot()) {
			return querySnapshotsForEq(Vaccination.IMMUNIZATION + "_id", immunization, Vaccination.CHANGE_DATE, false);
		}
		return queryForEq(Vaccination.IMMUNIZATION + "_id", immunization, Vaccination.CHANGE_DATE, false);
	}

	public long countByCriteria(VaccinationCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Vaccination");
			throw new RuntimeException(e);
		}
	}

	public List<Vaccination> queryByCriteria(VaccinationCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Vaccination.CREATION_DATE, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Vaccination");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Vaccination, Long> buildQueryBuilder(VaccinationCriteria criteria) throws SQLException {
		QueryBuilder<Vaccination, Long> queryBuilder = queryBuilder();
		Where<Vaccination, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		if (criteria.getImmunization() != null) {
			where.and().eq(Vaccination.IMMUNIZATION + "_id", criteria.getImmunization());
		}

		queryBuilder.setWhere(where);
		return queryBuilder;
	}

	@Override
	public Vaccination build() {
		throw new UnsupportedOperationException();
	}

	public Vaccination build(Immunization immunization) {
		Vaccination Vaccination = super.build();
		Vaccination.setImmunization(immunization);
		Vaccination.setReportingUser(ConfigProvider.getUser());
		return Vaccination;
	}
}
