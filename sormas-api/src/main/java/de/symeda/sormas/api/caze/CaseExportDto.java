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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class CaseExportDto implements Serializable {

	private static final long serialVersionUID = 8581579464816945555L;

	public static final String I18N_PREFIX = "CaseExport";

	private long id;
	private long personId;
	private long epiDataId;
	private long symptomsId;
	private String uuid;
	private String epidNumber;
	private String disease;
	private String person;
	private Sex sex;
	private String approximateAge;
	private Date reportDate;
	private String region;
	private String district;
	private String community;
	private Date admissionDate;
	private String healthFacility;
	private YesNoUnknown sampleTaken;
	private String sampleDates;
	private String labResults;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private CaseOutcome outcome;
	private Date deathDate;
	private String address;
	private String phone;
	private String occupationType;
	private String travelHistory;
	private YesNoUnknown contactWithRodent;
	private YesNoUnknown contactWithConfirmedCase;
	private Date onsetDate;
	private String symptoms;

	public CaseExportDto(long id, long personId, long epiDataId, long symptomsId, String uuid, String epidNumber, Disease disease, String diseaseDetails, String firstName, String lastName, 
			Sex sex, Integer approximateAge, ApproximateAgeType approximateAgeType, Date reportDate, String region, 
			String district, String community, Date admissionDate, String healthFacility, String healthFacilityUuid,
			String healthFacilityDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus, 
			PresentCondition presentCondition, CaseOutcome outcome, Date deathDate, String phone, String phoneOwner,
			OccupationType occupationType, String occupationDetails, String occupationFacility, String occupationFacilityUuid, String occupationFacilityDetails,
			YesNoUnknown contactWithRodent, YesNoUnknown contactWithConfirmedCase, Date onsetDate) {
		this.id = id;
		this.personId = personId;
		this.epiDataId = epiDataId;
		this.symptomsId = symptomsId;
		this.uuid = uuid;
		this.epidNumber = epidNumber;
		this.disease = DiseaseHelper.toString(disease, diseaseDetails);
		this.person = PersonDto.buildCaption(firstName, lastName);
		this.sex = sex;
		this.approximateAge = PersonHelper.buildAgeString(approximateAge, approximateAgeType);
		this.reportDate = reportDate;
		this.region = region;
		this.district = district;
		this.community = community;
		this.admissionDate = admissionDate;
		this.healthFacility = FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacility, healthFacilityDetails);
		this.caseClassification = caseClassification;
		this.investigationStatus = investigationStatus;
		this.presentCondition = presentCondition;
		this.outcome = outcome;
		this.deathDate = deathDate;
		this.phone = PersonHelper.buildPhoneString(phone, phoneOwner);
		this.occupationType = PersonHelper.buildOccupationString(occupationType, occupationDetails,
				FacilityHelper.buildFacilityString(occupationFacilityUuid, occupationFacility, occupationFacilityDetails));
		this.contactWithRodent = contactWithRodent;
		this.contactWithConfirmedCase = contactWithConfirmedCase;
		this.onsetDate = onsetDate;
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(uuid, person);
	}

	public long getId() {
		return id;
	}

	public long getPersonId() {
		return personId;
	}

	public long getEpiDataId() {
		return epiDataId;
	}

	public long getSymptomsId() {
		return symptomsId;
	}
	
	@Order(0)
	public String getUuid() {
		return uuid;
	}

	@Order(1)
	public String getEpidNumber() {
		return epidNumber;
	}

	@Order(2)
	public String getDisease() {
		return disease;
	}

	@Order(3)
	public String getPerson() {
		return person;
	}

	@Order(4)
	public Sex getSex() {
		return sex;
	}

	@Order(5)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(6)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(7)
	public String getRegion() {
		return region;
	}

	@Order(8)
	public String getDistrict() {
		return district;
	}

	@Order(9)
	public String getCommunity() {
		return community;
	}

	@Order(10)
	public Date getAdmissionDate() {
		return admissionDate;
	}

	@Order(11)
	public String getHealthFacility() {
		return healthFacility;
	}

	@Order(12)
	public YesNoUnknown getSampleTaken() {
		return sampleTaken;
	}

	@Order(13)
	public String getSampleDates() {
		return sampleDates;
	}

	@Order(14)
	public String getLabResults() {
		return labResults;
	}

	@Order(15)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(16)
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	@Order(17)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(18)
	public CaseOutcome getOutcome() {
		return outcome;
	}

	@Order(19)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(20)
	public String getAddress() {
		return address;
	}

	@Order(21)
	public String getPhone() {
		return phone;
	}

	@Order(22)
	public String getOccupationType() {
		return occupationType;
	}

	@Order(23)
	public String getTravelHistory() {
		return travelHistory;
	}

	@Order(24)
	public YesNoUnknown getContactWithRodent() {
		return contactWithRodent;
	}

	@Order(25)
	public YesNoUnknown getContactWithConfirmedCase() {
		return contactWithConfirmedCase;
	}

	@Order(26)
	public Date getOnsetDate() {
		return onsetDate;
	}

	@Order(27)
	public String getSymptoms() {
		return symptoms;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public void setEpiDataId(long epiDataId) {
		this.epiDataId = epiDataId;
	}

	public void setSymptomsId(long symptomsId) {
		this.symptomsId = symptomsId;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setApproximateAge(String age) {
		this.approximateAge = age;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public void setHealthFacility(String healthFacility) {
		this.healthFacility = healthFacility;
	}

	public void setSampleTaken(YesNoUnknown sampleTaken) {
		this.sampleTaken = sampleTaken;
	}

	public void setSampleDates(String sampleDates) {
		this.sampleDates = sampleDates;
	}

	public void setSampleDates(List<Date> sampleDates) {
		StringBuilder sampleDateBuilder = new StringBuilder();
		for (int i = 0; i < sampleDates.size(); i++) {
			if (i > 0) {
				sampleDateBuilder.append(", ");
			}
			sampleDateBuilder.append(DateHelper.formatLocalShortDate(sampleDates.get(i)));
		}
		this.sampleDates = sampleDateBuilder.toString();
	}

	public void setLabResults(String labResults) {
		this.labResults = labResults;
	}

	public void setLabResults(List<SampleTestResultType> labResults) {
		StringBuilder testResultsBuilder = new StringBuilder();
		for (int i = 0; i < labResults.size(); i++) {
			if (i > 0) {
				testResultsBuilder.append(", ");
			}
			testResultsBuilder.append(labResults.get(i).toString());
		}
		this.labResults = testResultsBuilder.toString();
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}

	public void setTravelHistory(String travelHistory) {
		this.travelHistory = travelHistory;
	}

	public void setContactWithRodent(YesNoUnknown contactWithRodent) {
		this.contactWithRodent = contactWithRodent;
	}

	public void setContactWithConfirmedCase(YesNoUnknown contactWithConfirmedCase) {
		this.contactWithConfirmedCase = contactWithConfirmedCase;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

}
