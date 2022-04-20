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

package de.symeda.sormas.app;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;

public class TestHelper {

	public static final String REGION_UUID = "R1R1R1-R1R1R1-R1R1R1-R1R1R1R1";
	public static final String DISTRICT_UUID = "D1D1D1-D1D1D1-D1D1D1-D1D1D1D1";
	public static final String SECOND_DISTRICT_UUID = "D2D2D2-D2D2D2-D2D2D2-D2D2D2D2";
	public static final String COMMUNITY_UUID = "C1C1C1-C1C1C1-C1C1C1-C1C1C1C1";
	public static final String SECOND_COMMUNITY_UUID = "C2C2C2-C2C2C2-C2C2C2-C2C2C2C2";
	public static final String FACILITY_UUID = "F1F1F1-F1F1F1-F1F1F1-F1F1F1F1";
	public static final String SECOND_FACILITY_UUID = "F2F2F2-F2F2F2-F2F2F2-F2F2F2F2";
	public static final String LABORATORY_UUID = "L1L1L1-L1L1L1-L1L1L1-L1L1L1L1";
	public static final String COUNTRY_UUID = "C3C3C3-C3C3C3-C3C3C3-C3C3C3C3";
	public static final String USER_UUID = "0123456789";
	public static final String SECOND_USER_UUID = "0987654321";
	public static final String INFORMANT_USER_UUID = "0192837465";
	public static final String TEST_DATABASE_NAME = "test_sormas.db";

	public static Map<DefaultUserRole, UserRole> userRoleMap = new HashMap<>();

	public static void initTestEnvironment(boolean setInformantAsActiveUser) {
		// Initialize a testing context to not operate on the actual database
		Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

		// Initialize the testing database
		DatabaseHelper.init(context, TEST_DATABASE_NAME);
		// Make sure that no database/user is still set from the last run
		DatabaseHelper.clearTables(true);
		DatabaseHelper.clearConfigTable();
		ConfigProvider.clearUserLogin();
		ConfigProvider.init(context);

		ConfigProvider.setServerRestUrl("http://this-is-a-test-url-that-hopefully-doesnt-exist.com");

		try {
			insertInfrastructureData();
			insertOtherAndNoneFacilities();
			insertUsers(setInformantAsActiveUser);
		} catch (SQLException e) {
			Log.e(TestHelper.class.getSimpleName(), "Could not init test environment: " + e.getMessage());
		}
	}

	private static void insertUsers(boolean setInformantAsActiveUser) throws SQLException {
		// Create user and set username and password
		User user = new User();
		user.setUserName("SanaObas");
		user.setActive(true);
		user.setFirstName("Sana");
		user.setLastName("Obas");
		user.setUserRoles(new HashSet(Arrays.asList(getUserRoleMap().get(DefaultUserRole.SURVEILLANCE_OFFICER))));
		user.setCreationDate(new Date());
		user.setChangeDate(new Date());
		user.setUuid(USER_UUID);
		user.setRegion(DatabaseHelper.getRegionDao().queryUuid(REGION_UUID));
		user.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(DISTRICT_UUID));
		user.setJurisdictionLevel(JurisdictionLevel.DISTRICT);
		DatabaseHelper.getUserDao().create(user);
		if (!setInformantAsActiveUser) {
			ConfigProvider.setUsernameAndPassword("SanaObas", "TestPassword");
		}

		// Create a second user with a specific region and district
		User secondUser = new User();
		secondUser.setUserName("SaboAnas");
		secondUser.setActive(true);
		secondUser.setFirstName("Sabo");
		secondUser.setLastName("Anas");
		secondUser.setUserRoles(
			new HashSet(
				Arrays.asList(getUserRoleMap().get(DefaultUserRole.SURVEILLANCE_OFFICER), getUserRoleMap().get(DefaultUserRole.CASE_OFFICER))));
		secondUser.setCreationDate(new Date());
		secondUser.setChangeDate(new Date());
		secondUser.setUuid(SECOND_USER_UUID);
		secondUser.setRegion(DatabaseHelper.getRegionDao().queryUuid(REGION_UUID));
		secondUser.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(SECOND_DISTRICT_UUID));
		secondUser.setJurisdictionLevel(JurisdictionLevel.DISTRICT);
		DatabaseHelper.getUserDao().create(secondUser);

		// Create an informant
		User informant = new User();
		informant.setUserName("InfoUser");
		informant.setActive(true);
		informant.setFirstName("Info");
		informant.setLastName("User");
		informant.setUserRoles(new HashSet(Arrays.asList(getUserRoleMap().get(DefaultUserRole.HOSPITAL_INFORMANT))));
		informant.setCreationDate(new Date());
		informant.setChangeDate(new Date());
		informant.setUuid(INFORMANT_USER_UUID);
		informant.setRegion(DatabaseHelper.getRegionDao().queryUuid(REGION_UUID));
		informant.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(DISTRICT_UUID));
		informant.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(FACILITY_UUID));
		informant.setJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY);
		DatabaseHelper.getUserDao().create(informant);
		if (setInformantAsActiveUser) {
			ConfigProvider.setUsernameAndPassword("InfoUser", "TestPassword");
		}
	}

	private static void insertInfrastructureData() throws SQLException {
		// Create example country, region, district, community and health facility
		Country country = new Country();
		country.setCreationDate(new Date());
		country.setChangeDate(new Date());
		country.setName("Country");
		country.setUuid(COUNTRY_UUID);
		DatabaseHelper.getCountryDao().create(country);

		Region region = new Region();
		region.setCreationDate(new Date());
		region.setChangeDate(new Date());
		region.setName("Region");
		region.setUuid(REGION_UUID);
		DatabaseHelper.getRegionDao().create(region);

		District district = new District();
		district.setCreationDate(new Date());
		district.setChangeDate(new Date());
		district.setName("District");
		district.setUuid(DISTRICT_UUID);
		district.setRegion(region);
		DatabaseHelper.getDistrictDao().create(district);

		District secondDistrict = new District();
		secondDistrict.setCreationDate(new Date());
		secondDistrict.setChangeDate(new Date());
		secondDistrict.setName("Second District");
		secondDistrict.setUuid(SECOND_DISTRICT_UUID);
		secondDistrict.setRegion(region);
		DatabaseHelper.getDistrictDao().create(secondDistrict);

		Community community = new Community();
		community.setCreationDate(new Date());
		community.setChangeDate(new Date());
		community.setName("Community");
		community.setUuid(COMMUNITY_UUID);
		community.setDistrict(district);
		DatabaseHelper.getCommunityDao().create(community);

		Community secondCommunity = new Community();
		secondCommunity.setCreationDate(new Date());
		secondCommunity.setChangeDate(new Date());
		secondCommunity.setName("Second Community");
		secondCommunity.setUuid(SECOND_COMMUNITY_UUID);
		secondCommunity.setDistrict(secondDistrict);
		DatabaseHelper.getCommunityDao().create(secondCommunity);

		Facility laboratory = new Facility();
		laboratory.setCreationDate(new Date());
		laboratory.setChangeDate(new Date());
		laboratory.setName("A Laboratory");
		laboratory.setPublicOwnership(false);
		laboratory.setType(FacilityType.LABORATORY);
		laboratory.setUuid(LABORATORY_UUID);
		laboratory.setRegion(region);
		laboratory.setDistrict(district);
		laboratory.setCity("Laboratory City");
		DatabaseHelper.getFacilityDao().create(laboratory);

		Facility facility = new Facility();
		facility.setCreationDate(new Date());
		facility.setChangeDate(new Date());
		facility.setName("Facility");
		facility.setPublicOwnership(false);
		facility.setUuid(FACILITY_UUID);
		facility.setType(FacilityType.HOSPITAL);
		facility.setRegion(region);
		facility.setDistrict(district);
		facility.setCommunity(community);
		facility.setCity("Facility City");
		facility.setLatitude(11.9327697753906D);
		facility.setLongitude(9.03450965881348D);
		DatabaseHelper.getFacilityDao().create(facility);

		Facility secondFacility = new Facility();
		secondFacility.setCreationDate(new Date());
		secondFacility.setChangeDate(new Date());
		secondFacility.setName("Second Facility");
		secondFacility.setPublicOwnership(false);
		secondFacility.setUuid(SECOND_FACILITY_UUID);
		secondFacility.setRegion(region);
		secondFacility.setDistrict(secondDistrict);
		secondFacility.setCommunity(secondCommunity);
		secondFacility.setCity("Second Facility City");
		secondFacility.setLatitude(12.9327697753906D);
		secondFacility.setLongitude(10.03450965881348D);
		DatabaseHelper.getFacilityDao().create(secondFacility);
	}

	private static void insertOtherAndNoneFacilities() throws SQLException {
		Facility otherFacility = new Facility();
		otherFacility.setCreationDate(new Date());
		otherFacility.setChangeDate(new Date());
		otherFacility.setName("Other Facility");
		otherFacility.setPublicOwnership(false);
		otherFacility.setUuid(FacilityDto.OTHER_FACILITY_UUID);
		DatabaseHelper.getFacilityDao().create(otherFacility);

		Facility noneFacility = new Facility();
		noneFacility.setCreationDate(new Date());
		noneFacility.setChangeDate(new Date());
		noneFacility.setName("None Facility");
		noneFacility.setPublicOwnership(false);
		noneFacility.setUuid(FacilityDto.NONE_FACILITY_UUID);
		DatabaseHelper.getFacilityDao().create(noneFacility);
	}

	public static Map<DefaultUserRole, UserRole> getUserRoleMap() {
		if (userRoleMap.isEmpty()) {
			createUserRoles();
		}
		return userRoleMap;
	}

	private static void createUserRoles() {
		Arrays.stream(DefaultUserRole.values()).forEach(defaultUserRole -> {
			UserRole userRole = new UserRole();
			userRole.setUserRights(defaultUserRole.getDefaultUserRights());
			userRole.setCaption(defaultUserRole.toString());
			userRole.setPortHealthUser(defaultUserRole.isPortHealthUser());
			userRole.setHasAssociatedOfficer(defaultUserRole.hasAssociatedOfficer());
			userRole.setHasOptionalHealthFacility(defaultUserRole.hasOptionalHealthFacility());
			userRole.setJurisdictionLevel(defaultUserRole.getJurisdictionLevel());
			try {
				userRole = DatabaseHelper.getUserRoleDao().saveAndSnapshot(userRole);
			} catch (DaoException e) {
				e.printStackTrace();
			}
			userRoleMap.put(defaultUserRole, userRole);
		});
	}
}
