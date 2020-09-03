package de.symeda.sormas.app.util;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.TestHelper;

@RunWith(AndroidJUnit4.class)
public class DiseaseConfigurationCacheTest {

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void testInit() {
		DiseaseConfigurationCache cache = DiseaseConfigurationCache.getInstance();

		List<Disease> activeDiseases = cache.getAllActiveDiseases();

		assertTrue(activeDiseases.size() > 0);
	}
}
