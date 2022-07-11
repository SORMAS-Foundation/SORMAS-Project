package de.symeda.sormas.backend.disease;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
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
		creator.updateDiseaseConfiguration(Disease.MALARIA, true, true, false); // invalid
		getBean(DiseaseConfigurationFacadeEjbLocal.class).loadData();

		List<Disease> diseases = getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, false, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertTrue(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
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

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertTrue(diseases.contains(Disease.DIPHTERIA));
		assertTrue(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(false, null, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertTrue(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(false, null, false);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertTrue(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		/** check limited disease **/
		TestDataCreator.RDCF rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto limitedDiseaseUser = creator.createUser(
				rdcf1,
				"Surv",
				"Off1",
				Disease.EVD,
				creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		loginWith(limitedDiseaseUser);

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertTrue(diseases.contains(Disease.DIPHTERIA));
		assertTrue(diseases.contains(Disease.MALARIA));
	}
}
