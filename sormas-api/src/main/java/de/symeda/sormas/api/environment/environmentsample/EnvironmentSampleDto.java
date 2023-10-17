/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.environment.environmentsample;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.ENVIRONMENT_MANAGEMENT)
public class EnvironmentSampleDto extends PseudonymizableDto {

	private static final long serialVersionUID = 4199710123573825998L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 2000;

	public static final String I18N_PREFIX = "EnvironmentSample";

	public static final String ENVIRONMENT = "environment";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String OTHER_SAMPLE_MATERIAL = "otherSampleMaterial";
	public static final String SAMPLE_VOLUME = "sampleVolume";
	public static final String FIELD_SAMPLE_ID = "fieldSampleId";
	public static final String TURBIDITY = "turbidity";
	public static final String PH_VALUE = "phValue";
	public static final String SAMPLE_TEMPERATURE = "sampleTemperature";
	public static final String CHLORINE_RESIDUALS = "chlorineResiduals";
	public static final String LABORATORY = "laboratory";
	public static final String LABORATORY_DETAILS = "laboratoryDetails";
	public static final String REQUESTED_PATHOGEN_TESTS = "requestedPathogenTests";
	public static final String OTHER_REQUESTED_PATHOGEN_TESTS = "otherRequestedPathogenTests";
	public static final String WEATHER_CONDITIONS = "weatherConditions";
	public static final String HEAVY_RAIN = "heavyRain";
	public static final String DISPATCHED = "dispatched";
	public static final String DISPATCH_DATE = "dispatchDate";
	public static final String DISPATCH_DETAILS = "dispatchDetails";
	public static final String RECEIVED = "received";
	public static final String RECEIVAL_DATE = "receivalDate";
	public static final String LAB_SAMPLE_ID = "labSampleId";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String LOCATION = "location";
	public static final String GENERAL_COMMENT = "generalComment";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@NotNull
	private EnvironmentReferenceDto environment;
	@NotNull
	private Date reportDate;
	@NotNull
	private UserReferenceDto reportingUser;
	@NotNull
	private Date sampleDateTime;
	@NotNull
	private EnvironmentSampleMaterial sampleMaterial;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherSampleMaterial;
	@Min(value = 0, message = Validations.numberTooSmall)
	private Float sampleVolume;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String fieldSampleId;
	@Min(value = 0, message = Validations.numberTooSmall)
	private Integer turbidity;
	@Min(value = 0, message = Validations.numberTooSmall)
	@Max(value = 14, message = Validations.numberTooBig)
	private Integer phValue;
	private Integer sampleTemperature;
	@Min(value = 0, message = Validations.numberTooSmall)
	private Float chlorineResiduals;
	@NotNull
	@SensitiveData
	private FacilityReferenceDto laboratory;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String laboratoryDetails;
	private Set<Pathogen> requestedPathogenTests;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherRequestedPathogenTests;
	private Map<WeatherCondition, Boolean> weatherConditions;
	private YesNoUnknown heavyRain;
	private boolean dispatched;
	private Date dispatchDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String dispatchDetails;
	private boolean received;
	private Date receivalDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String labSampleId;
	private SpecimenCondition specimenCondition;
	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private LocationDto location;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String generalComment;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static EnvironmentSampleDto build(EnvironmentReferenceDto environment, UserReferenceDto reportingUser) {
		EnvironmentSampleDto sample = new EnvironmentSampleDto();

		sample.setUuid(DataHelper.createUuid());
		sample.setEnvironment(environment);
		sample.setWeatherConditions(new HashMap<>());
		sample.setReportDate(new Date());
		sample.setReportingUser(reportingUser);
		sample.setSampleDateTime(new Date());
		sample.setLocation(LocationDto.build());

		return sample;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public EnvironmentSampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(EnvironmentSampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public String getOtherSampleMaterial() {
		return otherSampleMaterial;
	}

	public void setOtherSampleMaterial(String otherSampleMaterial) {
		this.otherSampleMaterial = otherSampleMaterial;
	}

	public Float getSampleVolume() {
		return sampleVolume;
	}

	public void setSampleVolume(Float sampleVolume) {
		this.sampleVolume = sampleVolume;
	}

	public String getFieldSampleId() {
		return fieldSampleId;
	}

	public void setFieldSampleId(String fieldSampleId) {
		this.fieldSampleId = fieldSampleId;
	}

	public Integer getTurbidity() {
		return turbidity;
	}

	public void setTurbidity(Integer turbidity) {
		this.turbidity = turbidity;
	}

	public Integer getPhValue() {
		return phValue;
	}

	public void setPhValue(Integer phValue) {
		this.phValue = phValue;
	}

	public Integer getSampleTemperature() {
		return sampleTemperature;
	}

	public void setSampleTemperature(Integer sampleTemperature) {
		this.sampleTemperature = sampleTemperature;
	}

	public Float getChlorineResiduals() {
		return chlorineResiduals;
	}

	public void setChlorineResiduals(Float chlorineResiduals) {
		this.chlorineResiduals = chlorineResiduals;
	}

	public FacilityReferenceDto getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(FacilityReferenceDto laboratory) {
		this.laboratory = laboratory;
	}

	public String getLaboratoryDetails() {
		return laboratoryDetails;
	}

	public void setLaboratoryDetails(String laboratoryDetails) {
		this.laboratoryDetails = laboratoryDetails;
	}

	public Set<Pathogen> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<Pathogen> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	public String getOtherRequestedPathogenTests() {
		return otherRequestedPathogenTests;
	}

	public void setOtherRequestedPathogenTests(String otherRequestedPathogenTests) {
		this.otherRequestedPathogenTests = otherRequestedPathogenTests;
	}

	public Map<WeatherCondition, Boolean> getWeatherConditions() {
		return weatherConditions;
	}

	public void setWeatherConditions(Map<WeatherCondition, Boolean> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	public YesNoUnknown getHeavyRain() {
		return heavyRain;
	}

	public void setHeavyRain(YesNoUnknown heavyRain) {
		this.heavyRain = heavyRain;
	}

	public boolean isDispatched() {
		return dispatched;
	}

	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getDispatchDetails() {
		return dispatchDetails;
	}

	public void setDispatchDetails(String dispatchDetails) {
		this.dispatchDetails = dispatchDetails;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public Date getReceivalDate() {
		return receivalDate;
	}

	public void setReceivalDate(Date receivalDate) {
		this.receivalDate = receivalDate;
	}

	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public LocationDto getLocation() {
		return location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public String getGeneralComment() {
		return generalComment;
	}

	public void setGeneralComment(String generalComment) {
		this.generalComment = generalComment;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	public EnvironmentSampleReferenceDto toReference() {
		return new EnvironmentSampleReferenceDto(getUuid());
	}
}
