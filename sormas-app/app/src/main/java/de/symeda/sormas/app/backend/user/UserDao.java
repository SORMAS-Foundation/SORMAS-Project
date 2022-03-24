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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
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
			where.and(where.eq(User.REGION + "_id", region.getId()), createRoleFilter(role, where));

			return (List<User>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByRegionAndRole");
			throw new RuntimeException(e);
		}
	}

	public List<User> getByDistrictAndRole(District district, UserRole role) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(where.eq(User.DISTRICT + "_id", district.getId()), createRoleFilter(role, where));

			return (List<User>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByDistrictAndRole");
			throw new RuntimeException(e);
		}
	}

	public List<User> getByDistrictAndRole(District district, UserRole role, String orderBy) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(where.eq(User.DISTRICT + "_id", district.getId()), createRoleFilter(role, where));

			return (List<User>) builder.orderBy(orderBy, true).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getByDistrictAndRole");
			throw new RuntimeException(e);
		}
	}

	public List<User> getAllInJurisdiction() {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(AbstractDomainObject.SNAPSHOT, false);

			User currentUser = ConfigProvider.getUser();

			// create role filters for national and regional users
			List<UserRole> nationalOrRegionalRoles = UserRole.getWithJurisdictionLevels(JurisdictionLevel.NATION, JurisdictionLevel.REGION);
			for (int i = 0; i < nationalOrRegionalRoles.size(); i++) {
				UserRole role = nationalOrRegionalRoles.get(i);
				createRoleFilter(role, where);
			}
			where.or(nationalOrRegionalRoles.size());

			// conjunction by default, jurisdiction filter should not be empty because of combining role and jurisdiction filters by OR
			// if the current user is a national one than it should see all users
			Where jurisdictionFilter = where.raw("1=1");
			if (currentUser.getHealthFacility() != null) {
				where.and(jurisdictionFilter, where.eq(User.HEALTH_FACILITY + "_id", currentUser.getHealthFacility()));
			} else if (currentUser.getPointOfEntry() != null) {
				where.and(jurisdictionFilter, where.eq(User.POINT_OF_ENTRY + "_id", currentUser.getPointOfEntry()));
			} else if (currentUser.getCommunity() != null) {
				where.and(jurisdictionFilter, where.eq(User.COMMUNITY + "_id", currentUser.getCommunity()));
			} else if (currentUser.getDistrict() != null) {
				where.and(jurisdictionFilter, where.eq(User.DISTRICT + "_id", currentUser.getDistrict()));
			} else if (currentUser.getRegion() != null) {
				where.and(jurisdictionFilter, where.eq(User.REGION + "_id", currentUser.getRegion()));
			}

			// combine role filters and jurisdiction filter with OR(=> roleFilter OR jurisdictionFilter)
			where.or(2);

			// combine snapshot filter with AND (=> snapshot AND (role OR jurisdiction))
			where.and(2);

			return builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getAllInJurisdiction");
			throw new RuntimeException(e);
		}
	}

	public List<User> getInformantsByAssociatedOfficer(User officer) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(User.ASSOCIATED_OFFICER + "_id", officer),
				where.or(createRoleFilter(UserRole.HOSPITAL_INFORMANT, where), createRoleFilter(UserRole.COMMUNITY_INFORMANT, where)));

			return (List<User>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getInformantsByAssociatedOfficer");
			throw new RuntimeException(e);
		}
	}

	// TODO: Potentially replace this with an API method in #4461
	public JurisdictionLevel getJurisdictionLevel(Collection<UserRole> roles) {

		boolean laboratoryJurisdictionPresent = false;
		for (UserRole role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	public User getRandomRegionUser(Region region, UserRight... userRights) {

		return getRandomUser(getUsersWithJurisdictionLevel(JurisdictionLevel.REGION, Arrays.asList(userRights)));
	}

	public User getRandomDistrictUser(District district, UserRight... userRights) {

		return getRandomUser(getUsersWithJurisdictionLevel(JurisdictionLevel.DISTRICT, Arrays.asList(userRights)));
	}

	private List<User> getUsersWithJurisdictionLevel(JurisdictionLevel jurisdictionLevel, Collection<UserRight> userRights) {

		try {
			QueryBuilder<User, Long> builder = queryBuilder();
			Where<User, Long> where = builder.where();
			where.eq(User.JURISDICTION_LEVEL, jurisdictionLevel);
			List<UserRole> userRoles = new ArrayList<>();
			if (userRights != null) {
				userRights.forEach(right -> userRoles.addAll(right.getDefaultUserRoles()));
			}

			if (userRoles.size() == 1) {
				where.and();
				createRoleFilter(userRoles.get(0), where);
			} else if (userRoles.size() > 1) {
				where.and();
				userRoles.forEach(role -> {
					try {
						createRoleFilter(role, where);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				});
				where.or(userRoles.size());
			}

			return builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getInformantsByAssociatedOfficer");
			throw new RuntimeException(e);
		}
	}

	private User getRandomUser(List<User> candidates) {

		if (CollectionUtils.isEmpty(candidates)) {
			return null;
		}

		return candidates.get(new Random().nextInt(candidates.size()));
	}

	private Where createRoleFilter(UserRole role, Where where) throws SQLException {
		return where.like(User.USER_ROLES_JSON, "%\"" + role.name() + "\"%");
	}

}
