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

import java.io.Serializable;

/**
 * Context object that holds configuration values looked up during Epipulse disease export.
 * This DTO encapsulates the configuration lookups that are common to all disease exports:
 * reporting country (the EpiPulse location code of the server country), subject code and data source.
 */
public class EpipulseConfigurationContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String reportingCountry;
	private final String subjectCode;
	private final String datasource;

	/**
	 * Creates a new configuration context.
	 *
	 * @param reportingCountry
	 *            the EpiPulse location code of the server country ({@code country.epipulselocationcode}), reported as
	 *            {@code ReportingCountry} and compared with the countries of birth, nationality and residence, e.g.
	 *            {@code "LU"}, {@code "EL"} for Greece
	 * @param subjectCode
	 *            the Epipulse subject code (e.g., "PERT", "MEAS")
	 * @param datasource
	 *            the Epipulse data source (e.g., "LU-SYSTEM1"), or {@code null} when none is configured
	 */
	public EpipulseConfigurationContext(String reportingCountry, String subjectCode, String datasource) {
		this.reportingCountry = reportingCountry;
		this.subjectCode = subjectCode;
		this.datasource = datasource;
	}

	public String getDatasource() {
		return datasource;
	}

	/** The EpiPulse location code of the server country, e.g. {@code "LU"}; see the constructor. */
	public String getReportingCountry() {
		return reportingCountry;
	}

	public String getSubjectCode() {
		return subjectCode;
	}
}
