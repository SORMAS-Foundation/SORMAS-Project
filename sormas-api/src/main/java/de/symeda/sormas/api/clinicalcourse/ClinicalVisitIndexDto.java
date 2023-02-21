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

package de.symeda.sormas.api.clinicalcourse;

import java.util.Date;

import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class ClinicalVisitIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = -7587908114350685830L;

	public static final String I18N_PREFIX = "ClinicalVisit";

	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISITING_PERSON = "visitingPerson";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String TEMPERATURE = "temperature";
	public static final String BLOOD_PRESSURE = "bloodPressure";
	public static final String HEART_RATE = "heartRate";
	public static final String SIGNS_AND_SYMPTOMS_COUNT = "signsAndSymptomsCount";

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

	private Boolean isInJurisdiction;

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
		boolean isInJurisdiction) {

		super(uuid);
		this.visitDateTime = visitDateTime;
		this.visitingPerson = visitingPerson;
		this.visitRemarks = visitRemarks;
		this.temperature = TemperatureSource.formatTemperatureSource(temperature, temperatureSource);
		this.bloodPressure = SymptomsHelper.getBloodPressureString(bloodPressureSystolic, bloodPressureDiastolic);
		this.heartRate = heartRate != null ? SymptomsHelper.getHeartRateString(heartRate) : "";
		this.symptomsId = symptomsId;
		this.isInJurisdiction = isInJurisdiction;
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

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
