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

package de.symeda.sormas.backend.epipulse.export;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.epipulse.AbstractEpipulseExportTest;

/**
 * Tests how {@link EpipulseConfigurationLookupService} reads the configuration of an EpiPulse export from the database: the
 * reporting country from {@code country.epipulselocationcode}, the subject code from
 * {@code diseaseconfiguration.epipulsesubjectcode} and the data source from {@code diseaseconfiguration.epipulsedatasource},
 * else the system configuration value {@code EPIPULSE_DATASOURCE}.
 *
 * <p>
 * Every test starts from the Luxembourg server of {@link AbstractEpipulseExportTest}: country "Luxembourg", ISO3
 * {@code LUX}, EpiPulse location code {@code LU}, locale {@code de-LU}; MEASLES configured with subject code {@code MEAS}
 * and its own data source {@code LU-SURV}; no system configuration value {@code EPIPULSE_DATASOURCE}.
 */
public class EpipulseConfigurationLookupServiceTest extends AbstractEpipulseExportTest {

	private EpipulseConfigurationLookupService lookupService;

	@BeforeEach
	public void setUpLookup() {

		configureLuxembourgFor(EpipulseSubjectCode.MEAS);
		lookupService = getBean(EpipulseConfigurationLookupService.class);
	}

	/**
	 * A fully configured disease: the context holds the EpiPulse location code of the server country, the subject code and
	 * the disease's own data source.
	 *
	 * <p>
	 * Example: MEAS on the Luxembourg server → reporting country {@code "LU"}, subject code {@code "MEAS"}, data source
	 * {@code "LU-SURV"}.
	 */
	@Test
	@DisplayName("a configured disease gets the location code, the subject code and its data source")
	public void configuredDiseaseGetsTheWholeContext() {

		EpipulseConfigurationContext context = lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.MEAS), SERVER_COUNTRY_NAME);

		assertThat(context.getReportingCountry(), is(SERVER_COUNTRY_LOCATION_CODE));
		assertThat(context.getSubjectCode(), is("MEAS"));
		assertThat(context.getDatasource(), is(SERVER_DATA_SOURCE));
	}

	/**
	 * Without {@code country.name}, the server country is found by the ISO3 code of the country part of
	 * {@code country.locale}.
	 *
	 * <p>
	 * Example: {@code country.name} empty, {@code country.locale=de-LU} → ISO3 {@code LUX} → Luxembourg → {@code "LU"}.
	 */
	@Test
	@DisplayName("without a country name the server country is found by the ISO3 code of the locale")
	public void serverCountryIsFoundByTheLocaleWithoutCountryName() {

		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_NAME, "");

		assertThat(lookupService.getServerCountryCode(), is(SERVER_COUNTRY_LOCATION_CODE));
	}

	/**
	 * An EpiPulse location code entered in lower case is reported in upper case.
	 *
	 * <p>
	 * Example: Luxembourg with {@code "lu"} → {@code "LU"}.
	 */
	@Test
	@DisplayName("a location code in lower case is reported in upper case")
	public void locationCodeIsReturnedInUpperCase() {

		setServerCountryLocationCode("lu");

		assertThat(lookupService.getServerCountryCode(), is("LU"));
	}

	/**
	 * An archived server country is never the reporting country.
	 *
	 * <p>
	 * Example: Luxembourg archived → {@code null}, also though it still has {@code "LU"}.
	 */
	@Test
	@DisplayName("an archived server country is not used")
	public void archivedServerCountryIsNotUsed() {

		archiveServerCountry();

		assertThat(lookupService.getServerCountryCode(), is(nullValue()));
	}

	/**
	 * A server country without EpiPulse location code makes the export fail with a message that tells the user where to
	 * enter it; there is no fallback to the NUTS code, the ISO2 code or the locale.
	 *
	 * <p>
	 * Example: Luxembourg without location code → "No EpiPulse location code is entered for the server country
	 * Luxembourg…".
	 */
	@Test
	@DisplayName("a server country without location code fails with a readable message")
	public void serverCountryWithoutLocationCodeFails() {

		setServerCountryLocationCode(null);

		ValidationRuntimeException exception = assertThrows(
			ValidationRuntimeException.class,
			() -> lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.MEAS), SERVER_COUNTRY_NAME));

		assertThat(exception.getMessage(), is(I18nProperties.getValidationError(Validations.epipulseReportingCountryMissing, SERVER_COUNTRY_NAME)));
	}

	/**
	 * A disease without subject code in its disease configuration makes the export fail with a message naming the disease.
	 *
	 * <p>
	 * Example: DIPHTHERIA without subject code → "No EpiPulse subject code is configured for Diphtheria. Enter it in the
	 * disease configuration of Diphtheria…".
	 */
	@Test
	@DisplayName("a disease without subject code fails with a readable message")
	public void missingSubjectCodeFails() {

		ValidationRuntimeException exception = assertThrows(
			ValidationRuntimeException.class,
			() -> lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.DIPH), SERVER_COUNTRY_NAME));

		String disease = String.valueOf(Disease.DIPHTHERIA);
		assertThat(exception.getMessage(), is(I18nProperties.getValidationError(Validations.epipulseSubjectCodeMissing, disease, disease)));
	}

	/**
	 * A subject code that belongs to another disease makes the export fail and names both codes.
	 *
	 * <p>
	 * Example: DIPHTHERIA configured with {@code "MEAS"}, export of {@code DIPH} → "… DIPH … Diphtheria … MEAS …".
	 */
	@Test
	@DisplayName("a subject code of another disease fails with a readable message")
	public void mismatchingSubjectCodeFails() {

		setDiseaseConfigurationValue(Disease.DIPHTHERIA, "epipulsesubjectcode", "MEAS");

		ValidationRuntimeException exception = assertThrows(
			ValidationRuntimeException.class,
			() -> lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.DIPH), SERVER_COUNTRY_NAME));

		String disease = String.valueOf(Disease.DIPHTHERIA);
		assertThat(
			exception.getMessage(),
			is(I18nProperties.getValidationError(Validations.epipulseSubjectCodeMismatch, "DIPH", disease, "MEAS", disease)));
	}

	/**
	 * The configured subject code is compared without regard to case.
	 *
	 * <p>
	 * Example: MEASLES configured with {@code "meas"} → the export uses {@code "MEAS"}.
	 */
	@Test
	@DisplayName("the subject code is compared without regard to case")
	public void subjectCodeIsComparedWithoutCase() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsesubjectcode", "meas");

		assertThat(lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.MEAS), SERVER_COUNTRY_NAME).getSubjectCode(), is("MEAS"));
	}

	/**
	 * Two subject codes that share one disease are both exportable, though the disease configuration holds only one.
	 *
	 * <p>
	 * Example: SYPHILIS configured with {@code "SYPH"} → an export of {@code CONSYPH} is accepted with {@code "CONSYPH"}.
	 */
	@Test
	@DisplayName("a sibling subject code of the same disease is accepted")
	public void siblingSubjectCodeIsAccepted() {

		configureLuxembourgForSyphilis();

		assertThat(
			lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.CONSYPH), SERVER_COUNTRY_NAME).getSubjectCode(),
			is("CONSYPH"));
	}

	/**
	 * The disease's own data source wins over the system configuration value.
	 *
	 * <p>
	 * Example: MEASLES with {@code "LU-SURV"}, {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code "LU-SURV"}.
	 */
	@Test
	@DisplayName("the disease's own data source wins over the system value")
	public void diseaseDatasourceWinsOverSystemValue() {

		setSystemDatasource("LU-SYSTEM1");

		assertThat(lookupService.lookupDatasource(EpipulseSubjectCode.MEAS), is(SERVER_DATA_SOURCE));
	}

	/**
	 * A disease without its own data source uses the system configuration value.
	 *
	 * <p>
	 * Example: MEASLES without own data source, {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code "LU-SYSTEM1"}.
	 */
	@Test
	@DisplayName("a disease without own data source uses the system value")
	public void diseaseWithoutDatasourceUsesSystemValue() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("LU-SYSTEM1");

		assertThat(lookupService.lookupDatasource(EpipulseSubjectCode.MEAS), is("LU-SYSTEM1"));
	}

	/**
	 * The empty system configuration value of a new installation counts as "not configured".
	 *
	 * <p>
	 * Example: MEASLES without own data source, {@code EPIPULSE_DATASOURCE = ""} → {@code null}.
	 */
	@Test
	@DisplayName("an empty system value counts as no data source")
	public void emptySystemValueIsNoDatasource() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("");

		assertThat(lookupService.lookupDatasource(EpipulseSubjectCode.MEAS), is(nullValue()));
		assertThat(lookupService.lookupDatasource(null), is(nullValue()));
	}

	/**
	 * Without any data source the export fails with a message that names the disease and the system configuration value.
	 *
	 * <p>
	 * Example: MEASLES without own data source and without system value → "An EpiPulse data source is required for
	 * Measles. Enter it in the disease configuration of Measles, or as system configuration value EPIPULSE_DATASOURCE."
	 */
	@Test
	@DisplayName("without any data source the export fails with a readable message")
	public void missingDatasourceFails() {

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);

		ValidationRuntimeException exception = assertThrows(
			ValidationRuntimeException.class,
			() -> lookupService.lookupConfiguration(exportOf(EpipulseSubjectCode.MEAS), SERVER_COUNTRY_NAME));

		assertThat(exception.getMessage(), is(lookupService.datasourceRequiredMessage(EpipulseSubjectCode.MEAS)));
		assertThat(exception.getMessage(), containsString(String.valueOf(Disease.MEASLES)));
		assertThat(exception.getMessage(), containsString(EpipulseConfigurationLookupService.DATASOURCE_CONFIG_KEY));
	}

	/**
	 * EpiPulse counts as configured as soon as the system value or one disease has a data source.
	 *
	 * <p>
	 * Example: no data source anywhere → {@code false}; only MEASLES with {@code "LU-SURV"} → {@code true}; only
	 * {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code true}.
	 */
	@Test
	@DisplayName("EpiPulse is configured when the system value or a disease has a data source")
	public void isDatasourceConfigured() {

		assertThat(lookupService.isDatasourceConfigured(), is(true));

		setDiseaseConfigurationValue(Disease.MEASLES, "epipulsedatasource", null);
		setSystemDatasource("");
		assertThat(lookupService.isDatasourceConfigured(), is(false));

		setSystemDatasource("LU-SYSTEM1");
		assertThat(lookupService.isDatasourceConfigured(), is(true));
	}

	/**
	 * An export without subject code is an internal error, not a configuration problem.
	 *
	 * <p>
	 * Example: {@code null} or an export without subject code → {@link IllegalArgumentException}.
	 */
	@Test
	@DisplayName("an export without subject code is rejected")
	public void exportWithoutSubjectCodeIsRejected() {

		assertThrows(IllegalArgumentException.class, () -> lookupService.lookupConfiguration(null, SERVER_COUNTRY_NAME));
		assertThrows(IllegalArgumentException.class, () -> lookupService.lookupConfiguration(new EpipulseExportDto(), SERVER_COUNTRY_NAME));
	}

	/**
	 * Configures SYPHILIS with {@code "SYPH"} only, so {@code CONSYPH} has to be accepted as its sibling code.
	 */
	private void configureLuxembourgForSyphilis() {
		setDiseaseConfigurationValue(Disease.SYPHILIS, "epipulsesubjectcode", "SYPH");
		setDiseaseConfigurationValue(Disease.SYPHILIS, "epipulsedatasource", SERVER_DATA_SOURCE);
	}

	/** An export request for {@code subjectCode}, e.g. {@code MEAS}. */
	private EpipulseExportDto exportOf(EpipulseSubjectCode subjectCode) {

		EpipulseExportDto exportDto = new EpipulseExportDto();
		exportDto.setSubjectCode(subjectCode);
		return exportDto;
	}
}
