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

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;

public class VaccinationDao extends AbstractAdoDao<VaccinationEntity> {

	public VaccinationDao(Dao<VaccinationEntity, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<VaccinationEntity> getAdoClass() {
		return VaccinationEntity.class;
	}

	@Override
	public String getTableName() {
		return VaccinationEntity.TABLE_NAME;
	}


	public long countByCriteria(VaccinationCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Vaccination");
			throw new RuntimeException(e);
		}
	}

	public List<VaccinationEntity> queryByCriteria(VaccinationCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(VaccinationEntity.CREATION_DATE, true).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Vaccination");
			throw new RuntimeException(e);
		}
	}


	private QueryBuilder<VaccinationEntity, Long> buildQueryBuilder(VaccinationCriteria criteria) throws SQLException {
		QueryBuilder<VaccinationEntity, Long> queryBuilder = queryBuilder();
		Where<VaccinationEntity, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);

		if (criteria.getImmunization() != null) {
			where.and().eq(VaccinationEntity.IMMUNIZATION + "_id", criteria.getImmunization());
		}

		queryBuilder.setWhere(where);
		return queryBuilder;
	}



	@Override
	public VaccinationEntity build() {
		throw new UnsupportedOperationException();
	}

	public VaccinationEntity build(Immunization immunization) {
		VaccinationEntity vaccinationEntity = super.build();
		vaccinationEntity.setImmunization(immunization);
		vaccinationEntity.setReportingUser(ConfigProvider.getUser());
		return vaccinationEntity;
	}
}
