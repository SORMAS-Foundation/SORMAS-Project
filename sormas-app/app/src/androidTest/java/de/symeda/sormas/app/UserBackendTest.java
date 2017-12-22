package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.user.User;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class UserBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void shouldGetByDistrictAndRole() {
        assertThat(DatabaseHelper.getUserDao().queryForAll().size(), is(3));

        District district = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);

        assertThat(DatabaseHelper.getUserDao().getByDistrictAndRole(district,  UserRole.SURVEILLANCE_OFFICER, User.FIRST_NAME).size(), is(1));
        List<User> informants = DatabaseHelper.getUserDao().getByDistrictAndRole(district, UserRole.INFORMANT, User.FIRST_NAME);
        assertThat(informants.size(), is(1));
        assertThat(informants.get(0).getUserRoles(), contains(UserRole.INFORMANT));
    }

}
