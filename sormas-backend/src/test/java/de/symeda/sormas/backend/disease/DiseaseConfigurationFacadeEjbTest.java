package de.symeda.sormas.backend.disease;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;

public class DiseaseConfigurationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllDiseases() {

		creator.updateDiseaseConfiguration(Disease.EVD, true, true, true);
		creator.updateDiseaseConfiguration(Disease.CHOLERA, true, false, true);
		creator.updateDiseaseConfiguration(Disease.DENGUE, false, true, true);
		creator.updateDiseaseConfiguration(Disease.LASSA, false, false, false);
		creator.updateDiseaseConfiguration(Disease.DIPHTERIA, true, false, false);
		creator.updateDiseaseConfiguration(Disease.MALARIA, true, true, false);
		getBean(DiseaseConfigurationFacadeEjbLocal.class).loadData();

		List<Disease> diseases = getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, true, null);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertTrue(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, false, null);
		assertFalse(diseases.contains(Disease.EVD));
		assertTrue(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertTrue(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(false, true, null);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertTrue(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertTrue(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(null, null, null);
		assertTrue(diseases.isEmpty());
	}
}
