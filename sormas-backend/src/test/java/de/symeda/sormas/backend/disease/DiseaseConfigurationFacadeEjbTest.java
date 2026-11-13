/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.disease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;

public class DiseaseConfigurationFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllDiseases() {

		creator.updateDiseaseConfiguration(Disease.EVD, true, true, true, false, null, true, 2, 21);
		creator.updateDiseaseConfiguration(Disease.CHOLERA, true, false, true, false, null, true, 0, 5);
		creator.updateDiseaseConfiguration(Disease.DENGUE, false, true, true, false, null, true, 2, 14);
		creator.updateDiseaseConfiguration(Disease.LASSA, false, false, false, true, null, true, 3, 21);
		creator.updateDiseaseConfiguration(Disease.DIPHTHERIA, true, false, false, true, null, true, 1, 10);
		creator.updateDiseaseConfiguration(Disease.MALARIA, true, true, false, true, null, true, 7, 60);
		creator.updateDiseaseConfiguration(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, true, true, true, false, null, true, 1, 10);
		creator.updateDiseaseConfiguration(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, true, true, true, false, null, true, 1, 3);
		getBean(DiseaseConfigurationFacadeEjbLocal.class).loadData();

		List<Disease> diseases = getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));
		assertTrue(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertTrue(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));
		assertTrue(diseases.contains(Disease.SHIGELLOSIS));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, false, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertTrue(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));
		assertFalse(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertFalse(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertTrue(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));
		assertTrue(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertTrue(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, false, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertTrue(diseases.contains(Disease.DIPHTHERIA));
		assertTrue(diseases.contains(Disease.MALARIA));
		assertFalse(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertFalse(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(false, null, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertTrue(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));
		assertFalse(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertFalse(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(false, null, false, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertTrue(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));
		assertFalse(diseases.contains(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		assertFalse(diseases.contains(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION));

		/** check limited disease **/
		TestDataCreator.RDCF rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto limitedDiseaseUser =
			creator.createUser(rdcf1, "Surv", "Off1", Disease.EVD, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		loginWith(limitedDiseaseUser);

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, true);
		assertTrue(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertFalse(diseases.contains(Disease.DIPHTHERIA));
		assertFalse(diseases.contains(Disease.MALARIA));

		diseases = getDiseaseConfigurationFacade().getAllDiseases(true, null, false, true);
		assertFalse(diseases.contains(Disease.EVD));
		assertFalse(diseases.contains(Disease.CHOLERA));
		assertFalse(diseases.contains(Disease.DENGUE));
		assertFalse(diseases.contains(Disease.LASSA));
		assertTrue(diseases.contains(Disease.DIPHTHERIA));
		assertTrue(diseases.contains(Disease.MALARIA));
	}

	@Test
	public void presetMakesRubellaPrimaryOnLuxembourgEvenWhenFalseWasStored() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		storeRubellaConfigurationAsSavedByTheAdminForm();

		getBean(DiseaseConfigurationFacadeEjbLocal.class).presetLuxembourgRubellaConfiguration();

		DiseaseConfiguration rubella = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.RUBELLA);
		assertTrue(rubella.getActive());
		assertTrue(rubella.getPrimaryDisease());
		assertTrue(rubella.getCaseSurveillanceEnabled());
		assertEquals(EnumSet.of(ExposureCategory.RESPIRATORY, ExposureCategory.VERTICAL_TRANSMISSION), rubella.getExposureCategories());
		assertTrue(getDiseaseConfigurationFacade().isPrimaryDisease(Disease.RUBELLA));
		assertTrue(getDiseaseConfigurationFacade().getAllDiseases(true, true, true).contains(Disease.RUBELLA));
	}

	@Test
	public void presetCreatesTheRubellaRowWhenItDoesNotExistYet() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		DiseaseConfiguration existing = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.RUBELLA);
		getDiseaseConfigurationService().deletePermanent(existing);

		getBean(DiseaseConfigurationFacadeEjbLocal.class).presetLuxembourgRubellaConfiguration();

		DiseaseConfiguration rubella = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.RUBELLA);
		assertTrue(rubella.getActive());
		assertTrue(rubella.getPrimaryDisease());
		assertTrue(rubella.getCaseSurveillanceEnabled());
		assertEquals(EnumSet.of(ExposureCategory.RESPIRATORY, ExposureCategory.VERTICAL_TRANSMISSION), rubella.getExposureCategories());
	}

	@Test
	public void presetLeavesRubellaUntouchedOutsideLuxembourg() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "de");
		storeRubellaConfigurationAsSavedByTheAdminForm();

		getBean(DiseaseConfigurationFacadeEjbLocal.class).presetLuxembourgRubellaConfiguration();

		DiseaseConfiguration rubella = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.RUBELLA);
		assertFalse(rubella.getPrimaryDisease());
		assertFalse(rubella.getCaseSurveillanceEnabled());
		assertEquals(EnumSet.of(ExposureCategory.AIR_BORNE, ExposureCategory.VERTICAL_TRANSMISSION), rubella.getExposureCategories());
	}

	/**
	 * The EpiPulse subject code and data source entered in the disease configuration are saved without surrounding spaces
	 * and returned by the facade.
	 *
	 * <p>
	 * Example: MEASLES saved with {@code " MEAS "} and {@code " LU-SURV "} → stored and returned as {@code "MEAS"} and
	 * {@code "LU-SURV"}.
	 */
	@Test
	public void epipulseSubjectCodeAndDatasourceAreSavedTrimmed() {

		DiseaseConfigurationDto measles = measlesConfiguration();
		measles.setEpipulseSubjectCode(" MEAS ");
		measles.setEpipulseDatasource(" LU-SURV ");
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(measles);

		DiseaseConfiguration stored = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.MEASLES);
		assertEquals("MEAS", stored.getEpipulseSubjectCode());
		assertEquals("LU-SURV", stored.getEpipulseDatasource());

		DiseaseConfigurationDto returned = measlesConfiguration();
		assertEquals("MEAS", returned.getEpipulseSubjectCode());
		assertEquals("LU-SURV", returned.getEpipulseDatasource());
	}

	/**
	 * An empty EpiPulse subject code or data source is stored as {@code null}, so the export treats it as "not configured"
	 * and falls back to the system configuration value {@code EPIPULSE_DATASOURCE} for the data source.
	 *
	 * <p>
	 * Example: MEASLES first saved with {@code "MEAS"} and {@code "LU-SURV"}, then with {@code "  "} and {@code ""} → both
	 * stored as {@code null}.
	 */
	@Test
	public void emptyEpipulseSubjectCodeAndDatasourceAreStoredAsNull() {

		DiseaseConfigurationDto measles = measlesConfiguration();
		measles.setEpipulseSubjectCode("MEAS");
		measles.setEpipulseDatasource("LU-SURV");
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(measles);

		measles = measlesConfiguration();
		measles.setEpipulseSubjectCode("  ");
		measles.setEpipulseDatasource("");
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(measles);

		DiseaseConfiguration stored = getDiseaseConfigurationService().getDiseaseConfiguration(Disease.MEASLES);
		assertNull(stored.getEpipulseSubjectCode());
		assertNull(stored.getEpipulseDatasource());
	}

	/** The disease configuration of MEASLES as the admin form loads it. */
	private DiseaseConfigurationDto measlesConfiguration() {
		return DiseaseConfigurationFacadeEjbLocal.toDto(getDiseaseConfigurationService().getDiseaseConfiguration(Disease.MEASLES));
	}

	private void storeRubellaConfigurationAsSavedByTheAdminForm() {
		DiseaseConfigurationDto rubella =
			DiseaseConfigurationFacadeEjbLocal.toDto(getDiseaseConfigurationService().getDiseaseConfiguration(Disease.RUBELLA));
		rubella.setActive(false);
		rubella.setPrimaryDisease(false);
		rubella.setCaseSurveillanceEnabled(false);
		rubella.setExposureCategories(EnumSet.of(ExposureCategory.AIR_BORNE, ExposureCategory.VERTICAL_TRANSMISSION));
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(rubella);
	}
}
