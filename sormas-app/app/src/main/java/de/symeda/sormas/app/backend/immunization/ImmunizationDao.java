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

package de.symeda.sormas.app.backend.immunization;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class ImmunizationDao extends AbstractAdoDao<Immunization> {

	public ImmunizationDao(Dao<Immunization, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Immunization> getAdoClass() {
		return Immunization.class;
	}

	@Override
	public String getTableName() {
		return Immunization.TABLE_NAME;
	}

	public List<Immunization> getAll() {
		try {
			QueryBuilder<Immunization, Long> queryBuilder = queryBuilder();
			return queryBuilder.orderBy(Immunization.CHANGE_DATE, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllActive on Immunization");
			throw new RuntimeException(e);
		}
	}

	public List<Immunization> queryByCriteria(ImmunizationCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Immunization.CHANGE_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Immunization");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(ImmunizationCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Immunization");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Immunization, Long> buildQueryBuilder(ImmunizationCriteria criteria) throws SQLException {
		QueryBuilder<Immunization, Long> queryBuilder = queryBuilder();

		List<Where<Immunization, Long>> whereStatements = new ArrayList<>();
		Where<Immunization, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		if (!whereStatements.isEmpty()) {
			Where<Immunization, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}

		return queryBuilder;
	}

	public void deleteImmunizationAndAllDependingEntities(String immunizationUuid) throws SQLException {
		Immunization immunization = queryUuidWithEmbedded(immunizationUuid);

		// Cancel if not in local database
		if (immunization == null) {
			return;
		}

		// Delete case
		deleteCascade(immunization);
	}

}
