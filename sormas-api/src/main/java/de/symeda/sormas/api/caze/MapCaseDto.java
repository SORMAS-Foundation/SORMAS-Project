/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;

public class MapCaseDto implements Serializable {

	private static final long serialVersionUID = -3021332968056368431L;

	public static final String I18N_PREFIX = "CaseData";

	private String uuid;
	private Date reportDate;
	private CaseClassification caseClassification;
	private Disease disease;
	private PersonReferenceDto person;
	@PersonalData
	@SensitiveData
	private Double healthFacilityLat;
	@PersonalData
	@SensitiveData
	private Double healthFacilityLon;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double reportLon;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double addressLat;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double addressLon;

	private CaseJurisdictionDto jurisdiction;

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
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid,
		String pointOfEntryUuid) {

		this.uuid = uuid;
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

		jurisdiction = new CaseJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, communityUuid, healthFacilityUuid, pointOfEntryUuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
		return jurisdiction.getHealthFacilityUuid();
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
		return person.toString() + " (" + DataHelper.getShortUuid(uuid) + ")";
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

	public CaseJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
