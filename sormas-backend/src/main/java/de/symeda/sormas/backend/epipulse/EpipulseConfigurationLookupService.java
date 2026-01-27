/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.epipulse;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.backend.epipulse.util.EpipulseConfigurationContext;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Service for looking up Epipulse configuration values from the database.
 * This service extracts the common configuration lookup logic that was duplicated
 * in both exportPertussisCaseBased and exportMeaslesCaseBased methods.
 */
@Stateless
@LocalBean
public class EpipulseConfigurationLookupService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	/**
	 * Looks up all required configuration values for an Epipulse export.
	 *
	 * @param exportDto
	 *            the export request DTO containing the subject code
	 * @param serverCountryLocale
	 *            the server country ISO2 code (e.g., "LU")
	 * @param serverCountryName
	 *            the server country name (e.g., "Luxembourg")
	 * @return a context object containing all looked-up configuration values
	 * @throws IllegalArgumentException
	 *             if the server country code is invalid
	 * @throws IllegalStateException
	 *             if the subject code lookup fails
	 */
	public EpipulseConfigurationContext lookupConfiguration(EpipulseExportDto exportDto, String serverCountryLocale, String serverCountryName)
		throws IllegalArgumentException, IllegalStateException {

		String reportingCountry = lookupReportingCountry(serverCountryLocale);
		String serverCountryNutsCode = lookupServerCountryNutsCode(serverCountryName);
		String subjectCode = lookupSubjectCode(exportDto.getSubjectCode());

		return new EpipulseConfigurationContext(reportingCountry, serverCountryNutsCode, subjectCode);
	}

	/**
	 * Looks up the Epipulse reporting country code for the given ISO2 country code.
	 *
	 * @param countryIso2Code
	 *            the ISO2 country code (e.g., "LU")
	 * @return the Epipulse reporting country code
	 * @throws IllegalArgumentException
	 *             if the country code is invalid
	 */
	private String lookupReportingCountry(String countryIso2Code) throws IllegalArgumentException {
		//@formatter:off
		String reportingCountryQuery =
			"select code as reporting_country " +
			"from epipulse_location_configuration " +
			"where type='Country' and country_iso2_code = :countryIso2Code";
		//@formatter:on

		@SuppressWarnings("unchecked")
		String reportingCountry = (String) em.createNativeQuery(reportingCountryQuery)
			.setParameter("countryIso2Code", countryIso2Code)
			.getResultStream()
			.filter(java.util.Objects::nonNull)
			.findFirst()
			.orElse(null);

		if (StringUtils.isBlank(reportingCountry)) {
			throw new IllegalArgumentException("Invalid server country code: " + countryIso2Code);
		}

		return reportingCountry;
	}

	/**
	 * Looks up the NUTS code for the given country name.
	 *
	 * @param countryName
	 *            the country name (e.g., "Luxembourg")
	 * @return the NUTS code for the country, or null if not found
	 */
	private String lookupServerCountryNutsCode(String countryName) {
		if (countryName == null) {
			return null;
		}

		//@formatter:off
		String serverCountryQuery =
			"select nutscode " +
			"from country " +
			"where lower(defaultname) = :countryName";
		//@formatter:on

		@SuppressWarnings("unchecked")
		String serverCountryNutsCode = (String) em.createNativeQuery(serverCountryQuery)
			.setParameter("countryName", countryName.toLowerCase())
			.getResultStream()
			.filter(java.util.Objects::nonNull)
			.findFirst()
			.orElse(null);

		return serverCountryNutsCode;
	}

	/**
	 * Looks up the Epipulse subject code for the given disease.
	 *
	 * @param subjectCodeEnum
	 *            the subject code enum value (e.g., PERT, MEAS)
	 * @return the Epipulse subject code string
	 * @throws IllegalStateException
	 *             if the subject code lookup fails
	 */
	private String lookupSubjectCode(EpipulseSubjectCode subjectCodeEnum) throws IllegalStateException {
		//@formatter:off
		String subjectCodeQuery =
			"select subjectcode " +
			"from epipulse_subjectcode_configuration " +
			"where disease=:disease and aggregatedreporting='No'";
		//@formatter:on

		@SuppressWarnings("unchecked")
		String subjectCode = (String) em.createNativeQuery(subjectCodeQuery)
			.setParameter("disease", subjectCodeEnum.name())
			.getResultStream()
			.filter(java.util.Objects::nonNull)
			.findFirst()
			.orElse(null);

		if (StringUtils.isBlank(subjectCode)) {
			throw new IllegalStateException("Subject code is empty");
		}

		return subjectCode;
	}
}
