/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.infrastructure;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public abstract class AbstractInfrastructureAdoDao<ADO extends InfrastructureAdo> extends AbstractAdoDao<ADO> {

	public AbstractInfrastructureAdoDao(Dao<ADO, Long> innerDao) {
		super(innerDao);
	}

	public List<ADO> queryActiveForAll(String orderBy, boolean ascending) {
		try {
			QueryBuilder<ADO, Long> builder = queryBuilder();
			Where<ADO, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));
			return builder.orderBy(orderBy, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForAll");
			throw new RuntimeException();
		}
	}

	public List<ADO> queryActiveForAll() {
		try {
			QueryBuilder<ADO, Long> builder = queryBuilder();
			Where<ADO, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));
			return builder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ADO> queryActiveForEq(String fieldName, Object value, String orderBy, boolean ascending) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false);
			where.and().eq(InfrastructureAdo.ARCHIVED, false).query();
			return builder.orderBy(orderBy, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

	public long countOfActive() {
		try {
			QueryBuilder<ADO, Long> builder = queryBuilder();
			Where<ADO, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));
			return builder.countOf();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

}
