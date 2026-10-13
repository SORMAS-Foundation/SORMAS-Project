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

package de.symeda.sormas.backend.epipulse.util;

import java.io.Serializable;

/**
 * Context object that holds configuration values looked up during Epipulse disease export.
 * This DTO encapsulates the three configuration lookups that are common to all disease exports:
 * reporting country, server country NUTS code, and subject code.
 */
public class EpipulseConfigurationContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String reportingCountry;
	private final String serverCountryNutsCode;
	private final String subjectCode;

	/**
	 * Creates a new configuration context.
	 *
	 * @param reportingCountry
	 *            the Epipulse reporting country code (e.g., "LU")
	 * @param serverCountryNutsCode
	 *            the NUTS code for the server country (e.g., "LU")
	 * @param subjectCode
	 *            the Epipulse subject code (e.g., "PERT", "MEAS")
	 */
	public EpipulseConfigurationContext(String reportingCountry, String serverCountryNutsCode, String subjectCode) {
		this.reportingCountry = reportingCountry;
		this.serverCountryNutsCode = serverCountryNutsCode;
		this.subjectCode = subjectCode;
	}

	public String getReportingCountry() {
		return reportingCountry;
	}

	public String getServerCountryNutsCode() {
		return serverCountryNutsCode;
	}

	public String getSubjectCode() {
		return subjectCode;
	}
}
