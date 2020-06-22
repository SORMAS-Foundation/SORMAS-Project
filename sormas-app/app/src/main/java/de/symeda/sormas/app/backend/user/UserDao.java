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

package de.symeda.sormas.app.backend.user;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class UserDao extends AbstractAdoDao<User> {

	public UserDao(Dao<User, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<User> getAdoClass() {
		return User.class;
	}

	@Override
	public String getTableName() {
		return User.TABLE_NAME;
	}

	public User getByUsername(String username) {
		List<User> users = queryForEq(User.USER_NAME, username);
		if (users.size() == 1) {
			User user = users.get(0);
			return user;
		} else if (users.size() == 0) {
			return null;
		} else {
			throw new RuntimeException("Found multiple users for name " + username);
		}
	}

	public List<User> getByRegionAndRole(Region region, UserRole role) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(where.eq(User.REGION + "_id", region.getId()), where.like(User.USER_ROLES_JSON, "%\"" + role.name() + "\"%"));

			return (List<User>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByRegionAndRole");
			throw new RuntimeException(e);
		}
	}

	public List<User> getAllInJurisdiction() {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(AbstractDomainObject.SNAPSHOT, false);

			User currentUser = ConfigProvider.getUser();
			UserRole.getJurisdictionLevel(currentUser.getUserRoles());

			if (currentUser.getHealthFacility() != null) {
				where.and().eq(User.HEALTH_FACILITY + "_id", currentUser.getHealthFacility());
			} else if (currentUser.getPointOfEntry() != null) {
				where.and().eq(User.POINT_OF_ENTRY + "_id", currentUser.getPointOfEntry());
			} else if (currentUser.getCommunity() != null) {
				where.and().eq(User.COMMUNITY + "_id", currentUser.getCommunity());
			} else if (currentUser.getDistrict() != null) {
				where.and().eq(User.DISTRICT + "_id", currentUser.getDistrict());
			} else if (currentUser.getRegion() != null) {
				where.and().eq(User.REGION + "_id", currentUser.getRegion());
			}

			return builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllInJurisdiction");
			throw new RuntimeException(e);
		}
	}

	public List<User> getByDistrictAndRole(District district, UserRole role, String orderBy) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(where.eq(User.DISTRICT + "_id", district.getId()), where.like(User.USER_ROLES_JSON, "%\"" + role.name() + "\"%"));

			return (List<User>) builder.orderBy(orderBy, true).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByDistrictAndRole");
			throw new RuntimeException(e);
		}
	}

	public List<User> getInformantsByAssociatedOfficer(User officer) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(User.ASSOCIATED_OFFICER + "_id", officer),
				where.or(
					where.like(User.USER_ROLES_JSON, "%\"" + UserRole.HOSPITAL_INFORMANT.name() + "\"%"),
					where.like(User.USER_ROLES_JSON, "%\"" + UserRole.COMMUNITY_INFORMANT.name() + "\"%")));

			return (List<User>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getInformantsByAssociatedOfficer");
			throw new RuntimeException(e);
		}
	}
}
