package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class CaseExportDto implements Serializable {

	private static final long serialVersionUID = 8581579464816945555L;

	public static final String I18N_PREFIX = "CaseExport";
	
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


	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(uuid, person);
	}
	
	@Override
	public String toString() {
		return CaseReferenceDto.buildCaption(uuid, person);
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

	public void setLabResults(String labResults) {
		this.labResults = labResults;
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
