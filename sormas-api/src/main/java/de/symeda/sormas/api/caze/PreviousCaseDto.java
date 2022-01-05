/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.disease.DiseaseVariant;

public class PreviousCaseDto implements Serializable {

	private static final long serialVersionUID = 5816724717269837258L;

	private final String uuid;
	private final Date reportDate;
	private final String externalToken;
	private final DiseaseVariant diseaseVariant;
	private final Date onsetDate;

	public PreviousCaseDto(String uuid, Date reportDate, String externalToken, DiseaseVariant diseaseVariant, Date onsetDate) {
		this.uuid = uuid;
		this.reportDate = reportDate;
		this.externalToken = externalToken;
		this.diseaseVariant = diseaseVariant;
		this.onsetDate = onsetDate;
	}

	public String getUuid() {
		return uuid;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public Date getOnsetDate() {
		return onsetDate;
	}
}
