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
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
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
	private long hospitalizationId;
	private String uuid;
	private String epidNumber;
	private String disease;
	private String person;
	private Sex sex;
	private String approximateAge;
	private String ageGroup;
	private Date reportDate;
	private String region;
	private String district;
	private String community;
	private Date admissionDate;
	private String healthFacility;
	private YesNoUnknown admittedToHealthFacility;
	private String initialDetectionPlace;
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
	private Vaccination vaccination;
	private String vaccinationDoses;
	private Date vaccinationDate;
	private VaccinationInfoSource vaccinationInfoSource;

	public CaseExportDto(long id, long personId, long epiDataId, long symptomsId, long hospitalizationId, String uuid, String epidNumber, Disease disease, String diseaseDetails, String firstName, String lastName, 
			Sex sex, Integer approximateAge, ApproximateAgeType approximateAgeType, Date reportDate, String region, 
			String district, String community, YesNoUnknown admittedToHealthFacility, Date admissionDate, String healthFacility, String healthFacilityUuid, String healthFacilityDetails, 
			CaseClassification caseClassification, InvestigationStatus investigationStatus, 
			PresentCondition presentCondition, CaseOutcome outcome, Date deathDate, String phone, String phoneOwner,
			OccupationType occupationType, String occupationDetails, String occupationFacility, String occupationFacilityUuid, String occupationFacilityDetails,
			YesNoUnknown contactWithRodent, YesNoUnknown contactWithConfirmedCase, Date onsetDate, Vaccination vaccination, String vaccinationDoses,
			Date vaccinationDate, VaccinationInfoSource vaccinationInfoSource) {
		this.id = id;
		this.personId = personId;
		this.epiDataId = epiDataId;
		this.symptomsId = symptomsId;
		this.hospitalizationId = hospitalizationId;
		this.uuid = uuid;
		this.epidNumber = epidNumber;
		this.disease = DiseaseHelper.toString(disease, diseaseDetails);
		this.person = PersonDto.buildCaption(firstName, lastName);
		this.sex = sex;
		this.approximateAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.ageGroup = ApproximateAgeHelper.getAgeGroupFromAge(approximateAge, approximateAgeType);
		this.reportDate = reportDate;
		this.region = region;
		this.district = district;
		this.community = community;
		this.admittedToHealthFacility = admittedToHealthFacility;
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
		this.vaccination = vaccination;
		this.vaccinationDoses = vaccinationDoses;
		this.vaccinationDate = vaccinationDate;
		this.vaccinationInfoSource = vaccinationInfoSource;
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
	
	public long getHospitalizationId() {
		return hospitalizationId;
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
	public String getAgeGroup() {
		return ageGroup;
	}

	@Order(7)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(8)
	public String getRegion() {
		return region;
	}

	@Order(9)
	public String getDistrict() {
		return district;
	}

	@Order(10)
	public String getCommunity() {
		return community;
	}
	
	@Order(11)
	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	@Order(12)
	public Date getAdmissionDate() {
		return admissionDate;
	}

	@Order(13)
	public String getHealthFacility() {
		return healthFacility;
	}
	
	@Order(14)
	public String getInitialDetectionPlace() {
		return initialDetectionPlace;
	}

	@Order(15)
	public YesNoUnknown getSampleTaken() {
		return sampleTaken;
	}

	@Order(16)
	public String getSampleDates() {
		return sampleDates;
	}

	@Order(17)
	public String getLabResults() {
		return labResults;
	}

	@Order(18)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(19)
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	@Order(20)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(21)
	public CaseOutcome getOutcome() {
		return outcome;
	}

	@Order(22)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(23)
	public String getAddress() {
		return address;
	}

	@Order(24)
	public String getPhone() {
		return phone;
	}

	@Order(25)
	public String getOccupationType() {
		return occupationType;
	}

	@Order(26)
	public String getTravelHistory() {
		return travelHistory;
	}

	@Order(27)
	public YesNoUnknown getContactWithRodent() {
		return contactWithRodent;
	}

	@Order(28)
	public YesNoUnknown getContactWithConfirmedCase() {
		return contactWithConfirmedCase;
	}
	
	@Order(29)
	public Vaccination getVaccination() {
		return vaccination;
	}
	
	@Order(30)
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}
	
	@Order(31)
	public Date getVaccinationDate() {
		return vaccinationDate;
	}
	
	@Order(32)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	@Order(33)
	public Date getOnsetDate() {
		return onsetDate;
	}

	@Order(34)
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
	
	public void setHospitalizationId(long hospitalizationId) {
		this.hospitalizationId = hospitalizationId;
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

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
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
	
	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
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
	
	public void setInitialDetectionPlace(String initialDetectionPlace) {
		this.initialDetectionPlace = initialDetectionPlace;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

}
