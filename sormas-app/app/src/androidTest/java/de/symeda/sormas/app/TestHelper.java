package de.symeda.sormas.app;

import android.app.Instrumentation;
import android.content.Context;
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
import de.symeda.sormas.app.caze.CasesActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */

public class TestHelper {

    public static void initTestEnvironment() {
        // Initialize a testing context to not operate on the actual database
        RenamingDelegatingContext context = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        // Make sure that no database/user is still set from the last run
        context.deleteDatabase("sormas.db");
        ConfigProvider.clearUsernameAndPassword();
        // Initialize the testing database
        DatabaseHelper.init(context);
        ConfigProvider.init(context);

        // Set the testEnvironment configuration object
        ConfigProvider.setTestEnvironment(true);

        insertUser();
        insertInfrastructureData();

        // Perform the login and make sure that it has worked
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(CasesActivity.class.getName(), null, false);

        onView(withId(R.id.login_user)).perform(typeText("SanaObas")).perform(closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("TestPassword")).perform(closeSoftKeyboard());
        onView(withId(R.id.action_login)).perform(click());

        CasesActivity nextActivity = (CasesActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(nextActivity);
    }

    public static void destroyTestEnvironment() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
        context.deleteDatabase("sormas.db");
        ConfigProvider.clearUsernameAndPassword();
    }

    private static void insertUser() {
        // Create user and set username and password
        User user = new User();
        user.setUserName("SanaObas");
        user.setAktiv(true);
        user.setFirstName("Sana");
        user.setLastName("Obas");
        user.setUserRole(UserRole.CASE_OFFICER);
        user.setCreationDate(new Date());
        user.setChangeDate(new Date());
        user.setUuid("1234567890");
        DatabaseHelper.getUserDao().create(user);
        ConfigProvider.setUsernameAndPassword("SanaObas", "TestPassword");
        ConfigProvider.setServerRestUrl("http://this-is-a-test-url-that-hopefully-doesnt-exist.com");
    }

    private static void insertInfrastructureData() {
        // Create example region, district, community and health facility
        Region region = new Region();
        region.setCreationDate(new Date());
        region.setChangeDate(new Date());
        region.setName("Kano");
        region.setUuid("UTJSQN-36GGNN-OBFACR-57LYSH7I");
        DatabaseHelper.getRegionDao().create(region);

        District district = new District();
        district.setCreationDate(new Date());
        district.setChangeDate(new Date());
        district.setName("Ajingi");
        district.setUuid("QL2H5V-M4SB23-VXBJ6L-GV6RKN4I");
        district.setRegion(region);
        DatabaseHelper.getDistrictDao().create(district);

        Community community = new Community();
        community.setCreationDate(new Date());
        community.setChangeDate(new Date());
        community.setName("Ajingi");
        community.setUuid("XWOJNR-FLCW6Q-DHYWEL-XN6BCFMI");
        community.setDistrict(district);
        DatabaseHelper.getCommunityDao().create(community);

        Facility facility = new Facility();
        facility.setCreationDate(new Date());
        facility.setChangeDate(new Date());
        facility.setName("Sakalawa Health Post");
        facility.setPublicOwnership(false);
        facility.setUuid("XXYZW2-3PODTL-LMWOB6-ITYGSCCI");
        facility.setRegion(region);
        facility.setDistrict(district);
        facility.setCommunity(community);
        facility.setCity("Sakalawa");
        facility.setLatitude(11.9327697753906f);
        facility.setLongitude(9.03450965881348f);
        DatabaseHelper.getFacilityDao().create(facility);
    }

}
