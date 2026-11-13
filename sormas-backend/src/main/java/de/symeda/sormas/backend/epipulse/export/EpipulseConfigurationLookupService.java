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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Reads the EpiPulse configuration that an export needs, only from the database:
 * <ul>
 * <li>the reporting country: {@code country.epipulselocationcode} of the server country (the export also reads it for the
 * country of birth, nationality, residence and place of infection)</li>
 * <li>the subject code: {@code diseaseconfiguration.epipulsesubjectcode}</li>
 * <li>the data source: {@code diseaseconfiguration.epipulsedatasource}, else the system configuration value
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

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	/**
	 * Looks up all required configuration values for an Epipulse export.
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
		String subjectCode = lookupSubjectCode(exportDto.getSubjectCode());

		// the data source is required: also checked when the export is created, this catches a value removed since then
		String datasource = lookupDatasource(exportDto.getSubjectCode());
		if (datasource == null) {
			throw new ValidationRuntimeException(datasourceRequiredMessage(exportDto.getSubjectCode()));
		}

		return new EpipulseConfigurationContext(reportingCountry, subjectCode, datasource);
	}

	/**
	 * The EpiPulse location code of the server country ({@code country.epipulselocationcode}), read from the country table
	 * only; {@code null} when the server country is not found or has no EpiPulse location code. The NUTS code is not used.
	 *
	 * <p>
	 * Only active countries are considered. The server country is the country whose default name is {@code country.name}
	 * (as {@code CountryFacade.getServerCountry()} finds it), else the country whose ISO3 code belongs to the country part
	 * of {@code country.locale} (see {@link #localeIso3Code(String)}).
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
			//@formatter:off
			locationCode = firstText(
				"select epipulselocationcode " +
				"from country " +
				"where lower(trim(defaultname)) = :value " +
				"  and coalesce(archived, false) = false " +
				"  and nullif(trim(epipulselocationcode), '') is not null " +
				"order by id",
				countryName.toLowerCase(Locale.ROOT));
			//@formatter:on
		}

		if (locationCode == null) {
			String iso3 = localeIso3Code(configFacade.getCountryLocale());
			if (iso3 != null) {
				//@formatter:off
				locationCode = firstText(
					"select epipulselocationcode " +
					"from country " +
					"where upper(trim(isocode)) = :value " +
					"  and coalesce(archived, false) = false " +
					"  and nullif(trim(epipulselocationcode), '') is not null " +
					"order by id",
					iso3);
				//@formatter:on
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
			//@formatter:off
			Object count = em.createNativeQuery(
				"select (select count(*) from systemconfigurationvalue " +
				"        where config_key = :key and nullif(trim(config_value), '') is not null) " +
				"     + (select count(*) from diseaseconfiguration " +
				"        where nullif(trim(epipulsedatasource), '') is not null)")
				.setParameter("key", DATASOURCE_CONFIG_KEY)
				.getSingleResult();
			//@formatter:on

			return count instanceof Number && ((Number) count).longValue() > 0;
		} catch (RuntimeException e) {
			logger.warn("Could not check the EpiPulse data source configuration: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Returns the EpiPulse data source for the given subject code.
	 *
	 * <p>
	 * Order: {@code diseaseconfiguration.epipulsedatasource} of {@link EpipulseSubjectCode#getDisease()}, then the system
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

		Disease disease = subjectCodeEnum.getDisease();
		if (disease != null) {
			String diseaseDatasource = firstText("select epipulsedatasource from diseaseconfiguration where disease = :value", disease.name());
			if (diseaseDatasource != null) {
				return diseaseDatasource;
			}
		}

		String defaultDatasource = firstText("select config_value from systemconfigurationvalue where config_key = :value", DATASOURCE_CONFIG_KEY);
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
	 * The trimmed text of the first non-null result of a single-column query with one parameter {@code :value}, or
	 * {@code null}.
	 */
	private String firstText(String query, String value) {

		@SuppressWarnings("unchecked")
		Object result = em.createNativeQuery(query).setParameter("value", value).getResultStream().filter(Objects::nonNull).findFirst().orElse(null);

		return result instanceof String ? StringUtils.trimToNull((String) result) : null;
	}

	/**
	 * Returns the EpiPulse reporting country code of the server country: its EpiPulse location code
	 * ({@code country.epipulselocationcode}). There is no fallback to the NUTS code or the server locale, so an export never
	 * reports a code that is not in the country table.
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
	 * The code is read from {@code diseaseconfiguration.epipulsesubjectcode} of {@link EpipulseSubjectCode#getDisease()}. It is
	 * accepted when it equals the configured code (case-insensitive). A code that shares its disease with another code and
	 * reports only a subset of its cases is also accepted when the configured code is one of those sibling codes: SYPHILIS
	 * holds one code, but SYPH and CONSYPH both have to be exportable.
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

		if (subjectCodeEnum.isAggregatedReporting()) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.epipulseSubjectCodeAggregated, diseaseCaption(subjectCodeEnum)));
		}

		Disease disease = subjectCodeEnum.getDisease();
		if (disease == null) {
			throw new IllegalStateException("No disease is assigned to subject code " + subjectCodeEnum.name());
		}

		String diseaseName = diseaseCaption(subjectCodeEnum);
		String configured = firstText("select epipulsesubjectcode from diseaseconfiguration where disease = :value", disease.name());
		if (configured == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.epipulseSubjectCodeMissing, diseaseName, diseaseName));
		}

		if (configured.equalsIgnoreCase(subjectCodeEnum.name())) {
			return subjectCodeEnum.name();
		}

		boolean configuredIsSiblingCode = Arrays.stream(EpipulseSubjectCode.values())
			.anyMatch(code -> code != subjectCodeEnum && code.getDisease() == disease && code.name().equalsIgnoreCase(configured));
		if (subjectCodeEnum.getCaseSubset() != null && configuredIsSiblingCode) {
			return subjectCodeEnum.name();
		}

		throw new ValidationRuntimeException(
			I18nProperties
				.getValidationError(Validations.epipulseSubjectCodeMismatch, subjectCodeEnum.name(), diseaseName, configured, diseaseName));
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
