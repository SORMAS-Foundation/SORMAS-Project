/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for geodetic location data related to a case")
public class MapCaseDto extends AbstractUuidDto {

	private static final long serialVersionUID = -3021332968056368431L;

	public static final String I18N_PREFIX = "CaseData";

	@Schema(description = "Date the case was reported")
	private Date reportDate;
	private CaseClassification caseClassification;
	private Disease disease;
	private PersonReferenceDto person;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Schema(description = "Geodetic latitude of the health facility responsible for the case")
	private Double healthFacilityLat;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Schema(description = "Geodetic longitude of the health facility responsible for the case")
	private Double healthFacilityLon;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Schema(description = "Geodetic latitude where the case was reported")
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Schema(description = "Geodetic longitude where the case was reported")
	private Double reportLon;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Schema(description = "Geodetic latitude of the persons home address")
	private Double addressLat;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Schema(description = "Geodetic latitude of the persons home address")
	private Double addressLon;

	@Schema(description = "Universal Id (UUID) of the health facility responsible for the case")
	private String healthFacilityUuid;

	@Schema(description = "Whether the DTO is in the user's jurisdiction. Used to determine which user right needs to be considered"
		+ "to decide whether sensitive and/or personal data is supposed to be shown.")
	private Boolean isInJurisdiction;

	public MapCaseDto(
		String uuid,
		Date reportDate,
		CaseClassification caseClassification,
		Disease disease,
		String personUuid,
		String personFirstName,
		String personLastName,
		String healthFacilityUuid,
		Double healthFacilityLat,
		Double healthFacilityLon,
		Double reportLat,
		Double reportLon,
		Double addressLat,
		Double addressLon,
		boolean isInJurisdiction) {

		super(uuid);
		this.reportDate = reportDate;
		this.caseClassification = caseClassification;
		this.disease = disease;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.setHealthFacilityLat(healthFacilityLat);
		this.setHealthFacilityLon(healthFacilityLon);
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.addressLat = addressLat;
		this.addressLon = addressLon;
		this.healthFacilityUuid = healthFacilityUuid;
		this.isInJurisdiction = isInJurisdiction;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getHealthFacilityUuid() {
		return healthFacilityUuid;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Double getAddressLat() {
		return addressLat;
	}

	public void setAddressLat(Double addressLat) {
		this.addressLat = addressLat;
	}

	public Double getAddressLon() {
		return addressLon;
	}

	public void setAddressLon(Double addressLon) {
		this.addressLon = addressLon;
	}

	@Override
	public String toString() {
		return person.toString() + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	public Double getHealthFacilityLat() {
		return healthFacilityLat;
	}

	public void setHealthFacilityLat(Double healthFacilityLat) {
		this.healthFacilityLat = healthFacilityLat;
	}

	public Double getHealthFacilityLon() {
		return healthFacilityLon;
	}

	public void setHealthFacilityLon(Double healthFacilityLon) {
		this.healthFacilityLon = healthFacilityLon;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
