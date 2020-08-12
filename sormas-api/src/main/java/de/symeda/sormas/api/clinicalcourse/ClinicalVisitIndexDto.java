package de.symeda.sormas.api.clinicalcourse;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.SensitiveData;

public class ClinicalVisitIndexDto implements Serializable {

	private static final long serialVersionUID = -7587908114350685830L;

	public static final String I18N_PREFIX = "ClinicalVisit";

	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISITING_PERSON = "visitingPerson";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String TEMPERATURE = "temperature";
	public static final String BLOOD_PRESSURE = "bloodPressure";
	public static final String HEART_RATE = "heartRate";
	public static final String SIGNS_AND_SYMPTOMS_COUNT = "signsAndSymptomsCount";

	private String uuid;
	private Date visitDateTime;
	@SensitiveData
	private String visitingPerson;
	@SensitiveData
	private String visitRemarks;
	private String temperature;
	private String bloodPressure;
	private String heartRate;
	private Integer signsAndSymptomsCount;
	private Long symptomsId;

	private CaseJurisdictionDto caseJurisdiction;

	public ClinicalVisitIndexDto(
		String uuid,
		Date visitDateTime,
		String visitingPerson,
		String visitRemarks,
		Float temperature,
		TemperatureSource temperatureSource,
		Integer bloodPressureSystolic,
		Integer bloodPressureDiastolic,
		Integer heartRate,
		Long symptomsId,
		String caseReportingUserUuid,
		String caseRegionUuid,
		String caseDistrictUuid,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this.uuid = uuid;
		this.visitDateTime = visitDateTime;
		this.visitingPerson = visitingPerson;
		this.visitRemarks = visitRemarks;
		this.temperature = TemperatureSource.formatTemperatureSource(temperature, temperatureSource);
		this.bloodPressure = SymptomsHelper.getBloodPressureString(bloodPressureSystolic, bloodPressureDiastolic);
		this.heartRate = heartRate != null ? SymptomsHelper.getHeartRateString(heartRate) : "";
		this.symptomsId = symptomsId;

		this.caseJurisdiction = new CaseJurisdictionDto(caseReportingUserUuid, caseRegionUuid, caseDistrictUuid, caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public String getVisitingPerson() {
		return visitingPerson;
	}

	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(String heartRate) {
		this.heartRate = heartRate;
	}

	public Integer getSignsAndSymptomsCount() {
		return signsAndSymptomsCount;
	}

	public void setSignsAndSymptomsCount(Integer signsAndSymptomsCount) {
		this.signsAndSymptomsCount = signsAndSymptomsCount;
	}

	public Long getSymptomsId() {
		return symptomsId;
	}

	public void setSymptomsId(Long symptomsId) {
		this.symptomsId = symptomsId;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}
}
