package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.District;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class LocationBackendTest {
    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void shouldGetLatLonString() {

        assertThat(Location.getLatLonString(179d, 15d, 0.1f), is("179, 15 +-0m"));

        assertThat(Location.getLatLonString(181d, 15d, 1.4f), is("181, 15 +-1m"));

        assertThat(Location.getLatLonString(-79d, 4033d, null), is("-79, 4033"));

        assertThat(Location.getLatLonString(null, 15d, 0.1f), is(""));
    }
}