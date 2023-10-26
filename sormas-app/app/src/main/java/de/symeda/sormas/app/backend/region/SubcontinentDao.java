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

package de.symeda.sormas.app.backend.region;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.infrastructure.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

public class SubcontinentDao extends AbstractInfrastructureAdoDao<Subcontinent> {

	public SubcontinentDao(Dao<Subcontinent, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Subcontinent> getAdoClass() {
		return Subcontinent.class;
	}

	@Override
	public String getTableName() {
		return Subcontinent.TABLE_NAME;
	}

	public List<Subcontinent> queryActiveByContinent(Continent continent) {
		try {
			QueryBuilder<Subcontinent, Long> builder = queryBuilder();
			Where<Subcontinent, Long> where = builder.where();
			where.and(
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(Subcontinent.CONTINENT + "_id", continent));

			return builder.orderBy(Subcontinent.DEFAULT_NAME, true).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryActiveByContinent");
			throw new RuntimeException(e);
		}
	}
}
