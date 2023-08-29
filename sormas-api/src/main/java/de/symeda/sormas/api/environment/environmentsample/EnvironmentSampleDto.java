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
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

	@NotNull
	private EnvironmentReferenceDto environment;
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

	public static EnvironmentSampleDto build(EnvironmentReferenceDto environment, UserReferenceDto reportingUser) {
		EnvironmentSampleDto sample = new EnvironmentSampleDto();

		sample.setUuid(DataHelper.createUuid());
		sample.setEnvironment(environment);
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
}
