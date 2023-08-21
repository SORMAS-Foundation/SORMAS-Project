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
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class UserDao extends AbstractAdoDao<User> {

	private Dao<UserUserRole, Long> userUserRoleDao;

	public UserDao(Dao<User, Long> innerDao, Dao<UserUserRole, Long> userUserRoleDao) throws SQLException {
		super(innerDao);
		this.userUserRoleDao = userUserRoleDao;
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
			initUserRoles(user);
			return user;
		} else if (users.size() == 0) {
			return null;
		} else {
			throw new RuntimeException("Found multiple users for name " + username);
		}
	}

	@Override
	public User querySnapshotByUuid(String uuid) {
		User user = super.querySnapshotByUuid(uuid);
		if (user != null) {
			initUserRoles(user);
		}
		return user;
	}

	@Override
	public User queryUuid(String uuid) {
		User user = super.queryUuid(uuid);
		if (user != null) {
			initUserRoles(user);
		}
		return user;
	}

	@Override
	public User queryForId(Long id) {
		User user = super.queryForId(id);
		if (user != null) {
			initUserRoles(user);
		}
		return user;
	}

	public void initUserRoles(User user) {
		user.setUserRoles(
			loadUserUserRoles(user.getId()).stream()
				.map(userUserRole -> DatabaseHelper.getUserRoleDao().queryForId(userUserRole.getUserRole().getId()))
				.collect(Collectors.toSet()));
	}

	private List<UserUserRole> loadUserUserRoles(Long userId) {
		try {
			QueryBuilder builder = userUserRoleDao.queryBuilder();
			Where where = builder.where();
			where.eq(UserUserRole.USER + "_id", userId);
			return (List<UserUserRole>) builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform loadUserRoles");
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
			String nationalOrRegionalRoles = DatabaseHelper.getUserRoleDao()
				.queryForAll()
				.stream()
				.filter(
					userRole -> JurisdictionLevel.NATION.equals(userRole.getJurisdictionLevel())
						|| JurisdictionLevel.REGION.equals(userRole.getJurisdictionLevel()))
				.map(userRole -> userRole.getId().toString())
				.collect(Collectors.joining(","));
			builder.join(userUserRoleDao.queryBuilder());
			where.raw(UserUserRole.TABLE_NAME + "." + UserUserRole.USER_ROLE + "_id in (" + nationalOrRegionalRoles + " )");

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

	public List<User> getUsersByAssociatedOfficer(User officer, UserRight userRight) {
		try {
			QueryBuilder<User, Long> builder = queryBuilder();
			Where<User, Long> where = builder.where();
			where.eq(User.ASSOCIATED_OFFICER + "_id", officer);
			addUserRightFilters(builder, userRight);
			return builder.query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getUsersByAssociatedOfficer");
			throw new RuntimeException(e);
		}
	}

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

		return getRandomUser(getUsersWithJurisdictionLevel(JurisdictionLevel.REGION, region, null, Arrays.asList(userRights)));
	}

	public User getRandomDistrictUser(District district, UserRight... userRights) {

		return getRandomUser(getUsersWithJurisdictionLevel(JurisdictionLevel.DISTRICT, null, district, Arrays.asList(userRights)));
	}

	public List<User> getUsersWithJurisdictionLevel(
		JurisdictionLevel jurisdictionLevel,
		Region region,
		District district,
		Collection<UserRight> userRights) {

		try {
			QueryBuilder<User, Long> builder = queryBuilder().distinct();
			Where<User, Long> where = builder.where();

			where.eq(User.JURISDICTION_LEVEL, jurisdictionLevel);

			if (region != null) {
				where.eq(User.REGION + "_id", region.getId());
			}
			if (district != null) {
				where.eq(User.DISTRICT + "_id", district.getId());
			}

			where.and(region != null && district != null ? 3 : region != null || district != null ? 2 : 1);

			addUserRightFilters(builder, (UserRight[]) userRights.toArray());

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

	private void addUserRightFilters(QueryBuilder<User, Long> userQB, UserRight... userRights) throws SQLException {
		List<UserRole> userRoles = new ArrayList<>();
		if (userRights != null) {
			Arrays.asList(userRights)
				.forEach(
					right -> userRoles.addAll(
						DatabaseHelper.getUserRoleDao()
							.queryForAll()
							.stream()
							.filter(userRole -> userRole.getUserRights().contains(right))
							.collect(Collectors.toList())));
		}

		createRolesFilter(userQB, userRoles);
	}

	private void createRolesFilter(QueryBuilder<User, Long> userQB, List<UserRole> roles) throws SQLException {
		QueryBuilder userrolesQB = userUserRoleDao.queryBuilder();
		userrolesQB.where().in(UserUserRole.USER_ROLE + "_id", roles);
		userQB.join(userrolesQB);
	}

	@Override
	public void create(User data) throws SQLException {
		if (data == null)
			return;
		super.create(data);
		if (data.getUserRoles() != null) {
			for (UserRole userRole : data.getUserRoles()) {
				int resultRowCount = userUserRoleDao.create(new UserUserRole(data, userRole));
				if (resultRowCount < 1)
					throw new SQLException(
						"Database entry was not created - go back and try again.\n" + "Type: " + UserUserRole.class.getSimpleName() + ", User-UUID: "
							+ data.getUuid());
			}
		}
	}

	@Override
	public void delete(User data) throws SQLException {
		if (data == null)
			return;

		DeleteBuilder<UserUserRole, Long> userUserRoleDeleteBuilder = userUserRoleDao.deleteBuilder();
		userUserRoleDeleteBuilder.where().eq(UserUserRole.USER + "_id", data);
		userUserRoleDeleteBuilder.delete();

		super.delete(data);
	}

	@Override
	protected void update(User data) throws SQLException {
		if (data == null)
			return;
		super.update(data);
		// 1. Delete existing UserUserRoles
		DeleteBuilder<UserUserRole, Long> userUserRoleDeleteBuilder = userUserRoleDao.deleteBuilder();
		userUserRoleDeleteBuilder.where().eq(UserUserRole.USER + "_id", data);
		userUserRoleDeleteBuilder.delete();

		// 2. Create new UserUserRoles
		if (data.getUserRoles() != null) {
			for (UserRole userRole : data.getUserRoles()) {
				int resultRowCount = userUserRoleDao.create(new UserUserRole(data, userRole));
				if (resultRowCount < 1)
					throw new SQLException(
						"Database entry was not created - go back and try again.\n" + "Type: " + UserUserRole.class.getSimpleName() + ", User-UUID: "
							+ data.getUuid());
			}
		}
	}
}
