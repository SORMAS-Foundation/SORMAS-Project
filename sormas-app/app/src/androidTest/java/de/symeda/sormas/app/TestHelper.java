package de.symeda.sormas.app;

import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import java.util.Date;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */

public class TestHelper {

    public static final String REGION_UUID = "R1R1R1-R1R1R1-R1R1R1-R1R1R1R1";
    public static final String DISTRICT_UUID = "D1D1D1-D1D1D1-D1D1D1-D1D1D1D1";
    public static final String SECOND_DISTRICT_UUID = "D2D2D2-D2D2D2-D2D2D2-D2D2D2D2";
    public static final String COMMUNITY_UUID = "C1C1C1-C1C1C1-C1C1C1-C1C1C1C1";
    public static final String SECOND_COMMUNITY_UUID = "C2C2C2-C2C2C2-C2C2C2-C2C2C2C2";
    public static final String FACILITY_UUID = "F1F1F1-F1F1F1-F1F1F1-F1F1F1F1";
    public static final String SECOND_FACILITY_UUID = "F2F2F2-F2F2F2-F2F2F2-F2F2F2F2";
    public static final String USER_UUID = "0123456789";
    public static final String SECOND_USER_UUID = "0987654321";
    public static final String INFORMANT_USER_UUID = "0192837465";

    public static void initTestEnvironment(boolean setInformantAsActiveUser) {
        // Initialize a testing context to not operate on the actual database
        RenamingDelegatingContext context = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        // Make sure that no database/user is still set from the last run
        context.deleteDatabase("sormas.db");
        ConfigProvider.clearUsernameAndPassword();
        // Initialize the testing database
        DatabaseHelper.init(context);
        ConfigProvider.init(context);

        insertInfrastructureData();
        insertUsers(setInformantAsActiveUser);
    }

    private static void insertUsers(boolean setInformantAsActiveUser) {
        // Create user and set username and password
        User user = new User();
        user.setUserName("SanaObas");
        user.setAktiv(true);
        user.setFirstName("Sana");
        user.setLastName("Obas");
        user.setUserRole(UserRole.CASE_OFFICER);
        user.setCreationDate(new Date());
        user.setChangeDate(new Date());
        user.setUuid(USER_UUID);
        DatabaseHelper.getUserDao().create(user);
        if (!setInformantAsActiveUser) {
            ConfigProvider.setUsernameAndPassword("SanaObas", "TestPassword");
            ConfigProvider.setServerRestUrl("http://this-is-a-test-url-that-hopefully-doesnt-exist.com");
        }

        // Create a second user with a specific region and district
        User secondUser = new User();
        secondUser.setUserName("SaboAnas");
        secondUser.setAktiv(true);
        secondUser.setFirstName("Sabo");
        secondUser.setLastName("Anas");
        secondUser.setUserRole(UserRole.SURVEILLANCE_OFFICER);
        secondUser.setCreationDate(new Date());
        secondUser.setChangeDate(new Date());
        secondUser.setUuid(SECOND_USER_UUID);
        secondUser.setRegion(DatabaseHelper.getRegionDao().queryUuid(REGION_UUID));
        secondUser.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(SECOND_DISTRICT_UUID));
        DatabaseHelper.getUserDao().create(secondUser);

        // Create an informant
        User informant = new User();
        informant.setUserName("InfoUser");
        informant.setAktiv(true);
        informant.setFirstName("Info");
        informant.setLastName("User");
        informant.setUserRole(UserRole.INFORMANT);
        informant.setCreationDate(new Date());
        informant.setChangeDate(new Date());
        informant.setUuid(INFORMANT_USER_UUID);
        informant.setRegion(DatabaseHelper.getRegionDao().queryUuid(REGION_UUID));
        informant.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(DISTRICT_UUID));
        informant.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(FACILITY_UUID));
        DatabaseHelper.getUserDao().create(informant);
        if (setInformantAsActiveUser) {
            ConfigProvider.setUsernameAndPassword("InfoUser", "TestPassword");
            ConfigProvider.setServerRestUrl("http://this-is-a-test-url-that-hopefully-doesnt-exist.com");
        }
    }

    private static void insertInfrastructureData() {
        // Create example region, district, community and health facility
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

        Facility facility = new Facility();
        facility.setCreationDate(new Date());
        facility.setChangeDate(new Date());
        facility.setName("Facility");
        facility.setPublicOwnership(false);
        facility.setUuid(FACILITY_UUID);
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

}
