package de.symeda.sormas.app.util;

import androidx.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;
import org.junit.Test;

import java.util.List;

import de.symeda.sormas.api.Disease;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DiseaseConfigurationCacheTest {

    @Test
    public void testInit(){
        DiseaseConfigurationCache cache = DiseaseConfigurationCache.getInstance();

        List<Disease> activeDiseases = cache.getAllActiveDiseases();

        assertNotNull(activeDiseases.get(0));
    }
}