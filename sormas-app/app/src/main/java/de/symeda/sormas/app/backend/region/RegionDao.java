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

package de.symeda.sormas.app.backend.region;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.infrastructure.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

public class RegionDao extends AbstractInfrastructureAdoDao<Region> {

	public RegionDao(Dao<Region, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Region> getAdoClass() {
		return Region.class;
	}

	@Override
	public String getTableName() {
		return Region.TABLE_NAME;
	}

	@Override
	public Region saveAndSnapshot(Region source) {
		throw new UnsupportedOperationException();
	}

	public List<Region> queryActiveByServerCountry() {
		String serverCountryName = ConfigProvider.getServerCountryName();
		Country serverCountry = null;
		if (serverCountryName != null) {
			List<Country> countries = DatabaseHelper.getCountryDao().queryActiveForEq(Country.NAME, serverCountryName, Country.NAME, true);
			serverCountry = countries.size() > 0 ? countries.get(0) : null;
		}

		try {
			QueryBuilder<Region, Long> builder = queryBuilder();
			Where<Region, Long> where = builder.where();
			where.and(where.eq(AbstractDomainObject.SNAPSHOT, false), where.eq(InfrastructureAdo.ARCHIVED, false));

			if (serverCountry != null) {
				where.and().eq(Region.COUNTRY + "_id", serverCountry).or().isNull(Region.COUNTRY + "_id");
			}

			return builder.orderBy(Region.NAME, true).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryActiveByServerCountry");
			throw new RuntimeException(e);
		}
	}

	public List<Region> queryActiveByCountry(Country country) {
		try {
			QueryBuilder<Region, Long> builder = queryBuilder();
			Where<Region, Long> where = builder.where();
			where.and(
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(Region.COUNTRY + "_id", country));

			return builder.orderBy(Region.NAME, true).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryActiveByCountry");
			throw new RuntimeException(e);
		}
	}

	public List<Region> queryActiveByArea(Area area) {
		try {
			QueryBuilder<Region, Long> builder = queryBuilder();
			Where<Region, Long> where = builder.where();
			where.and(
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(Region.AREA + "_id", area));

			return builder.orderBy(Region.NAME, true).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryActiveByArea");
			throw new RuntimeException(e);
		}
	}
}
