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

package de.symeda.sormas.app.backend.facility;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.AbstractInfrastructureAdoDao;
import de.symeda.sormas.app.backend.common.InfrastructureAdo;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

public class FacilityDao extends AbstractInfrastructureAdoDao<Facility> {

	public FacilityDao(Dao<Facility, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<Facility> getAdoClass() {
		return Facility.class;
	}

	@Override
	public String getTableName() {
		return Facility.TABLE_NAME;
	}

	/**
	 * @param region
	 *            null will return the change date of facilities that don't have a region
	 */
	public Date getLatestChangeDateByRegion(Region region) {

		try {
			QueryBuilder<Facility, Long> queryBuilder = queryBuilder();
			queryBuilder.selectRaw("MAX(" + AbstractDomainObject.CHANGE_DATE + ")");
			if (region != null) {
				queryBuilder.where().eq(Facility.REGION, region);
			} else {
				queryBuilder.where().isNull(Facility.REGION);
			}

			GenericRawResults<Object[]> maxChangeDateResult = queryRaw(
				queryBuilder.prepareStatementString(),
				new DataType[] {
					DataType.DATE_LONG });
			List<Object[]> dateResults = maxChangeDateResult.getResults();
			if (dateResults.size() > 0) {
				return (Date) dateResults.get(0)[0];
			}
			return null;

		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getLatestChangeDateByRegion");
			throw new RuntimeException();
		}
	}

	public List<Facility> getActiveHealthFacilitiesByDistrict(District district, boolean includeOtherFacility, boolean includeOtherPlace) {

		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(Facility.DISTRICT, district),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.or(where.ne(Facility.TYPE, FacilityType.LABORATORY), where.isNull(Facility.TYPE)));
			List<Facility> facilities = builder.orderBy(Facility.NAME, true).query();

			if (includeOtherFacility) {
				facilities.add(queryUuid(FacilityDto.OTHER_FACILITY_UUID));
			}
			if (includeOtherPlace) {
				facilities.add(queryUuid(FacilityDto.NONE_FACILITY_UUID));
			}

			return facilities;

		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform getActiveHealthFacilitiesByDistrict");
			throw new RuntimeException(e);
		}
	}

	public List<Facility> getActiveHealthFacilitiesByCommunity(Community community, boolean includeOtherFacility, boolean includeOtherPlace) {

		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(Facility.COMMUNITY, community),
				where.eq(InfrastructureAdo.ARCHIVED, false),
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.or(where.ne(Facility.TYPE, FacilityType.LABORATORY), where.isNull(Facility.TYPE)));
			List<Facility> facilities = builder.orderBy(Facility.NAME, true).query();

			if (includeOtherFacility) {
				facilities.add(queryUuid(FacilityDto.OTHER_FACILITY_UUID));
			}
			if (includeOtherPlace) {
				facilities.add(queryUuid(FacilityDto.NONE_FACILITY_UUID));
			}

			return facilities;

		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform getActiveHealthFacilitiesByCommunity");
			throw new RuntimeException(e);
		}
	}

	public List<Facility> getActiveLaboratories(boolean includeOtherLaboratory) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(Facility.TYPE, FacilityType.LABORATORY);
			where.and().eq(InfrastructureAdo.ARCHIVED, false);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false);
			where.and().ne(Facility.UUID, FacilityDto.OTHER_LABORATORY_UUID).query();
			List<Facility> facilities = builder.orderBy(Facility.NAME, true).query();

			if (includeOtherLaboratory) {
				facilities.add(queryUuid(FacilityDto.OTHER_LABORATORY_UUID));
			}

			return facilities;

		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Facility saveAndSnapshot(Facility facility) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Facility mergeOrCreate(Facility source) {
		throw new UnsupportedOperationException();
	}
}
