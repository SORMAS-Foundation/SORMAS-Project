package de.symeda.sormas.app.backend.disease;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.TestBackendActivity;
import de.symeda.sormas.app.TestHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DiseaseConfigurationDaoTest {

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void testCreateAndUpdate() throws DaoException {
        DiseaseConfigurationCache cache = DiseaseConfigurationCache.getInstance();

        Disease disease = cache.getAllActiveDiseases().get(0);

        DiseaseConfigurationDao dao = DatabaseHelper.getDiseaseConfigurationDao();

        DiseaseConfiguration configuration = dao.build();
        configuration.setDisease(disease);

        //Test create
        configuration.setActive(false);
        configuration.setFollowUpEnabled(true);

        dao.mergeOrCreate(configuration);

        cache = DiseaseConfigurationCache.getInstance();

        assertFalse(cache.getAllActiveDiseases().contains(disease));
        assertTrue(cache.getAllDiseasesWithFollowUp().contains(disease));

        //Test update
        configuration.setFollowUpEnabled(false);

        dao.mergeOrCreate(configuration);

        cache = DiseaseConfigurationCache.getInstance();

        assertFalse(cache.getAllActiveDiseases().contains(disease));
        assertFalse(cache.getAllDiseasesWithFollowUp().contains(disease));
    }
}