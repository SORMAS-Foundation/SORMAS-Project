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

package de.symeda.sormas.app.backend.infrastructure;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.common.InfrastructureAdo;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.District;

public class PointOfEntryDao extends AbstractInfrastructureAdoDao<PointOfEntry> {

	public PointOfEntryDao(Dao<PointOfEntry, Long> innerDao) {
		super(innerDao);
	}

	public List<PointOfEntry> getActiveByDistrict(District district, boolean includeOthers) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(PointOfEntry.DISTRICT + "_id", district.getId()),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.ne(PointOfEntry.ACTIVE, false));
			List<PointOfEntry> pointsOfEntry = builder.orderBy(PointOfEntry.NAME, true).query();

			if (includeOthers) {
				pointsOfEntry.add(queryUuid(PointOfEntryDto.OTHER_AIRPORT_UUID));
				pointsOfEntry.add(queryUuid(PointOfEntryDto.OTHER_SEAPORT_UUID));
				pointsOfEntry.add(queryUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID));
				pointsOfEntry.add(queryUuid(PointOfEntryDto.OTHER_POE_UUID));
			}

			return pointsOfEntry;
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform getActiveByDistrict");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks whether the database contains at least one active point of entry within the user's district.
	 */
	public boolean hasActiveEntriesInDistrict() {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(PointOfEntry.DISTRICT + "_id", ConfigProvider.getUser().getDistrict().getId()),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.ne(PointOfEntry.ACTIVE, false));
			return builder.queryForFirst() != null;
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform hasActiveEntriesInDistrict");
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<PointOfEntry> getAdoClass() {
		return PointOfEntry.class;
	}

	@Override
	public String getTableName() {
		return PointOfEntry.TABLE_NAME;
	}
}
