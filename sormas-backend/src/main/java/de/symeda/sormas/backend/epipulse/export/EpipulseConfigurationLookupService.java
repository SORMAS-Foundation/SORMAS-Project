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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValue;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValueService;

/**
 * Reads the EpiPulse configuration that an export needs, only from the database, through the entity services:
 * <ul>
 * <li>the reporting country: the EpiPulse location code ({@link Country#getEpipulseLocationCode()}) of the server country
 * (the export also reads it for the country of birth, nationality, residence and place of infection)</li>
 * <li>the subject code: {@link DiseaseConfiguration#getEpipulseSubjectCode()}</li>
 * <li>the data source: {@link DiseaseConfiguration#getEpipulseDatasource()}, else the system configuration value
 * {@link #DATASOURCE_CONFIG_KEY}</li>
 * </ul>
 * The EpiPulse location codes are entered by "Import default countries" or in the country form, the subject codes and data sources of
 * the diseases by an admin in the disease configuration, and the value of {@link #DATASOURCE_CONFIG_KEY} by an admin in the
 * system configuration; that key is created empty by schema version 687 in {@code sormas_schema.sql}.
 */
@Stateless
@LocalBean
public class EpipulseConfigurationLookupService {

	/**
	 * Key of the system configuration value holding the EpiPulse data source of every disease without its own one, e.g.
	 * {@code "LU-SYSTEM1"}; the key is created empty by schema version 687 in {@code sormas_schema.sql}, the value is
	 * entered by an admin.
	 */
	public static final String DATASOURCE_CONFIG_KEY = "EPIPULSE_DATASOURCE";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@EJB
	private CountryService countryService;

	@EJB
	private DiseaseConfigurationService diseaseConfigurationService;

	@EJB
	private SystemConfigurationValueService systemConfigurationValueService;

	/**
	 * Looks up all required configuration values for an Epipulse export.
	 * <p>
	 * The subject code and data source of the export (#14472) are used as they are: they were prefilled from the
	 * configuration when the export was created and may have been changed by the user for this export only. Every export
	 * created since #14472 has both (the export facade fills and requires them, and schema version 694 fills the subject code
	 * of older exports). Reading the configuration when one is empty is only a safety net, for an export created before
	 * #14472 that was still pending when the server was updated.
	 * <p>
	 * Example: export {@code CONSYPH} with subject code {@code "CONSYPH"} and data source {@code "LU-MANUAL"} → both are
	 * used, whatever the disease configuration of Syphilis holds; an export from before #14472 without a data source → the
	 * configured data source, as before.
	 *
	 * @param exportDto
	 *            the export request DTO containing the subject code
	 * @param serverCountryName
	 *            the server country name (e.g., "Luxembourg"), only used in the error message; the server country itself is
	 *            found by {@code country.name} and the ISO code of {@code country.locale}, see {@link #getServerCountryCode()}
	 * @return a context object containing all looked-up configuration values
	 * @throws ValidationRuntimeException
	 *             if the configuration is incomplete: the server country has no EpiPulse location code, the subject code is
	 *             missing in or does not match the disease configuration, or no data source is configured for the disease. Its
	 *             message tells the user what to enter where, and is shown as the reason of the failed export.
	 * @throws IllegalArgumentException
	 *             if the export has no subject code (an internal error)
	 * @throws IllegalStateException
	 *             if no disease is assigned to the subject code (an internal error)
	 */
	public EpipulseConfigurationContext lookupConfiguration(EpipulseExportDto exportDto, String serverCountryName)
		throws ValidationRuntimeException, IllegalArgumentException, IllegalStateException {

		if (exportDto == null || exportDto.getSubjectCode() == null) {
			throw new IllegalArgumentException("Subject code is required for Epipulse export");
		}

		String reportingCountry = requireReportingCountry(getServerCountryCode(), serverCountryName);

		// #14472 - the subject code entered for this export, else the disease configuration
		String subjectCode = StringUtils.trimToNull(exportDto.getExportSubjectCode());
		if (subjectCode != null) {
			// an entered code must not bypass the check that the code is reported case-based
			checkNotAggregated(exportDto.getSubjectCode());
		} else {
			subjectCode = lookupSubjectCode(exportDto.getSubjectCode());
		}

		// #14472 - the data source entered for this export, else the disease configuration and the system value.
		// The data source is required: also checked when the export is created, this catches a value removed since then
		String datasource = StringUtils.trimToNull(exportDto.getExportDatasource());
		if (datasource == null) {
			datasource = lookupDatasource(exportDto.getSubjectCode());
		}
		if (datasource == null) {
			throw new ValidationRuntimeException(datasourceRequiredMessage(exportDto.getSubjectCode()));
		}

		return new EpipulseConfigurationContext(reportingCountry, subjectCode, datasource);
	}

	/**
	 * The EpiPulse location code of the server country ({@link Country#getEpipulseLocationCode()}), read from the country
	 * table only; {@code null} when the server country is not found or has no EpiPulse location code. The NUTS code is not
	 * used.
	 *
	 * <p>
	 * Only active countries are considered. The server country is the country whose default name is {@code country.name}
	 * (found like {@code CountryFacade.getServerCountry()} does, by {@link CountryService#getByDefaultName(String, boolean)};
	 * of several matches the one with the lowest id that has an EpiPulse location code), else the country whose ISO3 code
	 * belongs to the country part of {@code country.locale} (see {@link #localeIso3Code(String)}).
	 *
	 * <p>
	 * Example: {@code country.name=Greece}, Greece with EpiPulse location code {@code "EL"} → {@code "EL"};
	 * {@code country.name} empty, {@code country.locale=el-GR} → ISO3 {@code GRC} → {@code "EL"}; Luxembourg without
	 * EpiPulse location code (also with NUTS code {@code "LU"}) → {@code null}; Luxembourg archived → {@code null}.
	 *
	 * @return the upper-case EpiPulse location code, or {@code null}
	 */
	public String getServerCountryCode() {

		String locationCode = null;

		String countryName = StringUtils.trimToNull(configFacade.getCountryName());
		if (countryName != null) {
			List<Country> countries = countryService.getByDefaultName(countryName, false);
			if (countries != null) {
				locationCode = countries.stream()
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(Country::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.map(country -> StringUtils.trimToNull(country.getEpipulseLocationCode()))
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
			}
		}

		if (locationCode == null) {
			String iso3 = localeIso3Code(configFacade.getCountryLocale());
			if (iso3 != null) {
				locationCode = countryService.getByIsoCode(iso3, false)
					.map(country -> StringUtils.trimToNull(country.getEpipulseLocationCode()))
					.orElse(null);
			}
		}

		return locationCode != null ? locationCode.toUpperCase(Locale.ROOT) : null;
	}

	/**
	 * Whether an EpiPulse data source is available for at least one disease: the system configuration value
	 * {@link #DATASOURCE_CONFIG_KEY} is not empty, or a disease configuration has its own data source.
	 *
	 * <p>
	 * Example: {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code true}; the value emptied by an admin and no disease with
	 * its own data source → {@code false}.
	 *
	 * @return {@code false} as well when the check itself fails, so callers treat EpiPulse as not configured
	 */
	public boolean isDatasourceConfigured() {

		try {
			if (getSystemDatasource() != null) {
				return true;
			}

			List<DiseaseConfiguration> configurations = diseaseConfigurationService.getAll();
			return configurations != null
				&& configurations.stream().anyMatch(configuration -> configuration != null && StringUtils.isNotBlank(configuration.getEpipulseDatasource()));
		} catch (RuntimeException e) {
			logger.warn("Could not check the EpiPulse data source configuration: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Returns the EpiPulse data source for the given subject code.
	 *
	 * <p>
	 * Order: {@link DiseaseConfiguration#getEpipulseDatasource()} of {@link EpipulseSubjectCode#getDisease()}, then the system
	 * configuration value {@link #DATASOURCE_CONFIG_KEY}.
	 *
	 * <p>
	 * Example: TUBERCULOSIS with data source {@code "LU-TUBE-REG"} → {@code TUBE} returns {@code "LU-TUBE-REG"}; MEASLES
	 * without one and {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code MEAS} returns {@code "LU-SYSTEM1"}.
	 *
	 * @return the data source, or {@code null} when neither is configured (or {@code subjectCodeEnum} is {@code null}); an
	 *         export then fails with {@link #datasourceRequiredMessage(EpipulseSubjectCode)}, as the data source is required
	 */
	public String lookupDatasource(EpipulseSubjectCode subjectCodeEnum) {

		if (subjectCodeEnum == null) {
			return null;
		}

		String diseaseDatasource = getDiseaseConfigurationValue(subjectCodeEnum.getDisease(), DiseaseConfiguration::getEpipulseDatasource);
		if (diseaseDatasource != null) {
			return diseaseDatasource;
		}

		String defaultDatasource = getSystemDatasource();
		if (defaultDatasource != null) {
			return defaultDatasource;
		}

		logger.warn(
			"No EpiPulse data source is configured for {}, neither in its disease configuration nor as system configuration value {}",
			subjectCodeEnum.name(),
			DATASOURCE_CONFIG_KEY);
		return null;
	}

	/**
	 * The message shown when no EpiPulse data source is configured for the disease of {@code subjectCode}, neither in its
	 * disease configuration nor as system configuration value {@link #DATASOURCE_CONFIG_KEY}.
	 *
	 * <p>
	 * Example: {@code MEAS} → "An EpiPulse data source is required for Measles. Enter it in the disease configuration of
	 * Measles, or as system configuration value EPIPULSE_DATASOURCE."; {@code null} → the same with an empty disease.
	 */
	public String datasourceRequiredMessage(EpipulseSubjectCode subjectCode) {

		String disease = diseaseCaption(subjectCode);
		return I18nProperties.getValidationError(Validations.epipulseDatasourceRequired, disease, disease, DATASOURCE_CONFIG_KEY);
	}

	/**
	 * The message shown when a new export has no EpiPulse subject code: none was entered and none is configured for the
	 * disease of {@code subjectCode} (#14472).
	 *
	 * <p>
	 * Example: {@code DIPH} → "No EpiPulse subject code is configured for Diphtheria. Enter it in the disease configuration
	 * of Diphtheria (field "EpiPulse subject code")."; {@code null} → the same with an empty disease.
	 */
	public String subjectCodeRequiredMessage(EpipulseSubjectCode subjectCode) {

		String disease = diseaseCaption(subjectCode);
		return I18nProperties.getValidationError(Validations.epipulseSubjectCodeMissing, disease, disease);
	}

	/**
	 * A trimmed value of the disease configuration of {@code disease}, read through
	 * {@link DiseaseConfigurationService#getDiseaseConfiguration(Disease)}.
	 * <p>
	 * Example: {@code (SYPHILIS, DiseaseConfiguration::getEpipulseSubjectCode)} with {@code " SYPH "} stored →
	 * {@code "SYPH"}; an empty value, no configuration for the disease or {@code null} disease → {@code null}.
	 *
	 * @param disease
	 *            the disease whose configuration is read
	 * @param getter
	 *            the value to read from the configuration
	 * @return the trimmed value, or {@code null}
	 */
	private String getDiseaseConfigurationValue(Disease disease, Function<DiseaseConfiguration, String> getter) {

		if (disease == null || getter == null) {
			return null;
		}

		DiseaseConfiguration configuration = diseaseConfigurationService.getDiseaseConfiguration(disease);
		return configuration != null ? StringUtils.trimToNull(getter.apply(configuration)) : null;
	}

	/**
	 * The trimmed system configuration value {@link #DATASOURCE_CONFIG_KEY}, read through
	 * {@link SystemConfigurationValueService} from the database (not from the cache of the system configuration facade, so
	 * a value written directly to the database is seen at once).
	 * <p>
	 * Example: {@code EPIPULSE_DATASOURCE = " LU-SYSTEM1 "} → {@code "LU-SYSTEM1"}; empty value or no such key →
	 * {@code null}.
	 *
	 * @return the trimmed value, or {@code null}
	 */
	private String getSystemDatasource() {

		List<SystemConfigurationValue> values = systemConfigurationValueService
			.getByPredicate((cb, root, cq) -> cb.equal(root.get(SystemConfigurationValue.KEY_FIELD_NAME), DATASOURCE_CONFIG_KEY));
		if (values == null) {
			return null;
		}

		return values.stream()
			.filter(Objects::nonNull)
			.map(value -> StringUtils.trimToNull(value.getValue()))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}

	/**
	 * Returns the EpiPulse reporting country code of the server country: its EpiPulse location code
	 * ({@link Country#getEpipulseLocationCode()}). There is no fallback to the NUTS code or the server locale, so an export
	 * never reports a code that is not in the country table.
	 *
	 * <p>
	 * Example: Greece with EpiPulse location code {@code "EL"} → {@code "EL"}; Luxembourg without an EpiPulse location code
	 * (also with NUTS code {@code "LU"} and locale {@code "de-LU"}) → throws, the export fails.
	 *
	 * @param serverCountryLocationCode
	 *            the EpiPulse location code of the server country, {@code null} when not found
	 * @param serverCountryName
	 *            the configured server country name, shown in the message; when empty, {@code country.locale} is shown
	 * @throws ValidationRuntimeException
	 *             if the server country has no EpiPulse location code, with the message "No EpiPulse location code is entered
	 *             for the server country Luxembourg. Enter it in the country, or import the default countries…"
	 */
	private String requireReportingCountry(String serverCountryLocationCode, String serverCountryName) throws ValidationRuntimeException {

		if (StringUtils.isNotBlank(serverCountryLocationCode)) {
			return serverCountryLocationCode.trim();
		}

		String country = StringUtils.defaultIfBlank(serverCountryName, StringUtils.defaultString(configFacade.getCountryLocale()));
		throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.epipulseReportingCountryMissing, country));
	}

	/**
	 * The country part of a locale as upper-case ISO 3166-1 alpha-2 code; {@code null} when the locale has no valid
	 * country part. The code is taken as written, not mapped to an EpiPulse code.
	 *
	 * <p>
	 * Example: {@code "de-LU"} → {@code "LU"}; {@code "fr_lu"} → {@code "LU"}; {@code "el-GR"} → {@code "GR"} (EpiPulse
	 * uses {@code "EL"}, which only {@code country.epipulselocationcode} holds); {@code "de"} → {@code null}; {@code ""} or
	 * {@code null} → {@code null}; {@code "de-1A"} → {@code null}.
	 */
	private String localeCountryCode(String locale) {

		String trimmed = StringUtils.trimToNull(locale);
		if (trimmed == null) {
			return null;
		}

		int separator = Math.max(trimmed.indexOf('-'), trimmed.indexOf('_'));
		if (separator < 0 || separator == trimmed.length() - 1) {
			return null;
		}

		String countryPart = trimmed.substring(separator + 1).trim();
		if (!countryPart.matches("[A-Za-z]{2}")) {
			return null;
		}

		return countryPart.toUpperCase(Locale.ROOT);
	}

	/**
	 * The ISO 3166-1 alpha-3 code of the country part of a locale, as stored in {@code country.isocode}; {@code null} when
	 * the locale has no valid country part.
	 *
	 * <p>
	 * Example: {@code "de-LU"} → {@code "LUX"}; {@code "fr_lu"} → {@code "LUX"}; {@code "el-GR"} → {@code "GRC"};
	 * {@code "en-GB"} → {@code "GBR"}; {@code "de"} → {@code null}; {@code ""} or {@code null} → {@code null};
	 * {@code "de-1A"} or an unknown country such as {@code "de-XY"} → {@code null}.
	 */
	private String localeIso3Code(String locale) {

		String countryPart = localeCountryCode(locale);
		if (countryPart == null) {
			return null;
		}

		try {
			String iso3 = new Locale("", countryPart).getISO3Country();
			return StringUtils.isNotBlank(iso3) ? iso3.toUpperCase(Locale.ROOT) : null;
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * Checks that the given subject code is enabled in the disease configuration of its disease and returns it.
	 *
	 * <p>
	 * The code is read from {@link DiseaseConfiguration#getEpipulseSubjectCode()} of {@link EpipulseSubjectCode#getDisease()}.
	 * It is accepted when it equals the configured code (case-insensitive). A code that shares its disease with another code
	 * and reports only a subset of its cases is also accepted when the configured code is one of those sibling codes:
	 * SYPHILIS holds one code, but SYPH and CONSYPH both have to be exportable.
	 *
	 * <p>
	 * Example: diseaseconfiguration of SYPHILIS has {@code "SYPH"} → {@code SYPH} returns {@code "SYPH"}, {@code CONSYPH}
	 * returns {@code "CONSYPH"}; diseaseconfiguration of DIPHTHERIA is empty → {@code DIPH} throws "No EpiPulse subject code
	 * is configured for Diphtheria. Enter it in the disease configuration of Diphtheria (field "EpiPulse subject code")."
	 *
	 * @param subjectCodeEnum
	 *            the subject code enum value (e.g., PERT, MEAS)
	 * @return the subject code, as the enum name (e.g. {@code "MEAS"})
	 * @throws ValidationRuntimeException
	 *             if the code is reported aggregated, or is missing in or does not match the disease configuration; the
	 *             message tells the user what to correct
	 * @throws IllegalStateException
	 *             if no disease is assigned to the subject code (an internal error)
	 */
	private String lookupSubjectCode(EpipulseSubjectCode subjectCodeEnum) throws ValidationRuntimeException, IllegalStateException {

		checkNotAggregated(subjectCodeEnum);

		Disease disease = subjectCodeEnum.getDisease();
		if (disease == null) {
			throw new IllegalStateException("No disease is assigned to subject code " + subjectCodeEnum.name());
		}

		String diseaseName = diseaseCaption(subjectCodeEnum);
		String configured = getDiseaseConfigurationValue(disease, DiseaseConfiguration::getEpipulseSubjectCode);
		if (configured == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.epipulseSubjectCodeMissing, diseaseName, diseaseName));
		}

		if (configured.equalsIgnoreCase(subjectCodeEnum.name())) {
			return subjectCodeEnum.name();
		}

		if (isSiblingSubsetCode(subjectCodeEnum, configured)) {
			return subjectCodeEnum.name();
		}

		throw new ValidationRuntimeException(
			I18nProperties
				.getValidationError(Validations.epipulseSubjectCodeMismatch, subjectCodeEnum.name(), diseaseName, configured, diseaseName));
	}

	/**
	 * The subject code the disease configuration provides for {@code subjectCodeEnum}, to prefill the export form (#14472).
	 * Applies the same rules as the export without an entered code, but never throws: a code that can't be resolved gives
	 * {@code null} (the form field stays empty and must be filled in), a configured code that matches neither the selected
	 * code nor a sibling is returned as it is, so the user sees and can correct it. Only reads the configuration.
	 * <p>
	 * Example: disease configuration of Syphilis has {@code "SYPH"} → {@code SYPH} returns {@code "SYPH"}, {@code CONSYPH}
	 * returns {@code "CONSYPH"}; Measles has {@code "MEASLES"} → {@code MEAS} returns {@code "MEASLES"}; nothing configured
	 * for Diphtheria → {@code DIPH} returns {@code null}; {@code null} → {@code null}.
	 *
	 * @param subjectCodeEnum
	 *            the code selected in the export form
	 * @return the subject code to prefill, or {@code null} when none can be provided
	 */
	public String findSubjectCode(EpipulseSubjectCode subjectCodeEnum) {

		if (subjectCodeEnum == null || subjectCodeEnum.isAggregatedReporting() || subjectCodeEnum.getDisease() == null) {
			return null;
		}

		String configured = getDiseaseConfigurationValue(subjectCodeEnum.getDisease(), DiseaseConfiguration::getEpipulseSubjectCode);
		if (configured == null) {
			return null;
		}

		if (configured.equalsIgnoreCase(subjectCodeEnum.name()) || isSiblingSubsetCode(subjectCodeEnum, configured)) {
			return subjectCodeEnum.name();
		}
		return configured;
	}

	/**
	 * Whether {@code configured} is the code of another subject code of the same disease while {@code subjectCodeEnum}
	 * reports only a subset of that disease's cases, so {@code subjectCodeEnum} is exportable with it.
	 * <p>
	 * Example: {@code CONSYPH} with configured {@code "SYPH"} → {@code true}; {@code MEAS} with {@code "MEASLES"} →
	 * {@code false}; {@code null} code or value → {@code false}.
	 */
	private boolean isSiblingSubsetCode(EpipulseSubjectCode subjectCodeEnum, String configured) {

		if (subjectCodeEnum == null || configured == null || subjectCodeEnum.getCaseSubset() == null) {
			return false;
		}

		Disease disease = subjectCodeEnum.getDisease();
		return Arrays.stream(EpipulseSubjectCode.values())
			.anyMatch(code -> code != subjectCodeEnum && code.getDisease() == disease && code.name().equalsIgnoreCase(configured));
	}

	/**
	 * Fails an export whose subject code EpiPulse expects as counts, as there is no aggregated export yet.
	 * <p>
	 * Example: no code is marked aggregated today, so this never throws; a code marked aggregated →
	 * {@link ValidationRuntimeException} with {@link Validations#epipulseSubjectCodeAggregated}.
	 *
	 * @throws ValidationRuntimeException
	 *             if the code is reported aggregated
	 */
	private void checkNotAggregated(EpipulseSubjectCode subjectCodeEnum) throws ValidationRuntimeException {

		if (subjectCodeEnum != null && subjectCodeEnum.isAggregatedReporting()) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.epipulseSubjectCodeAggregated, diseaseCaption(subjectCodeEnum)));
		}
	}

	/**
	 * The readable name of the disease of {@code subjectCode} for messages, else the subject code itself.
	 *
	 * <p>
	 * Example: {@code DIPH} → "Diphtheria"; {@code null} → "".
	 */
	private String diseaseCaption(EpipulseSubjectCode subjectCode) {

		if (subjectCode == null) {
			return "";
		}
		return subjectCode.getDisease() != null ? String.valueOf(subjectCode.getDisease()) : subjectCode.name();
	}
}
