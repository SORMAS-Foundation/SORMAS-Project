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

package de.symeda.sormas.backend.environment.environmentsample;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.environment.environmentsample.WeatherCondition;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity(name = "environmentsamples")
public class EnvironmentSample extends DeletableAdo {

	public static final String ENVIRONMENT = "environment";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String LABORATORY = "laboratory";
	public static final String LABORATORY_DETAILS = "laboratoryDetails";
	public static final String LOCATION = "location";
	public static final String DISPATCHED = "dispatched";
	public static final String DISPATCH_DATE = "dispatchDate";
	public static final String RECEIVED = "received";
	public static final String RECEIVAL_DATE = "receivalDate";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String OTHER_SAMPLE_MATERIAL = "otherSampleMaterial";
	public static final String FIELD_SAMPLE_ID = "fieldSampleId";
	public static final String PATHOGEN_TESTS = "pathogenTests";

	private static final long serialVersionUID = 7237701234186874155L;

	private Environment environment;
	private Date reportDate;
	private User reportingUser;
	private Date sampleDateTime;
	private EnvironmentSampleMaterial sampleMaterial;
	private String otherSampleMaterial;
	private Float sampleVolume;
	private String fieldSampleId;
	private Integer turbidity;
	private Integer phValue;
	private Integer sampleTemperature;
	private Float chlorineResiduals;
	private Facility laboratory;
	private String laboratoryDetails;
	private Set<Pathogen> requestedPathogenTests;
	private String otherRequestedPathogenTests;
	private Map<WeatherCondition, Boolean> weatherConditions;
	private YesNoUnknown heavyRain;
	private boolean dispatched;
	private Date dispatchDate;
	private String dispatchDetails;
	private boolean received;
	private Date receivalDate;
	private String labSampleId;
	private SpecimenCondition specimenCondition;
	private Location location;
	private String generalComment;

	private List<PathogenTest> pathogenTests;

	@ManyToOne(optional = false)
	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public EnvironmentSampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(EnvironmentSampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getOtherSampleMaterial() {
		return otherSampleMaterial;
	}

	public void setOtherSampleMaterial(String otherSampleMaterial) {
		this.otherSampleMaterial = otherSampleMaterial;
	}

	@Column
	public Float getSampleVolume() {
		return sampleVolume;
	}

	public void setSampleVolume(Float sampleVolume) {
		this.sampleVolume = sampleVolume;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getFieldSampleId() {
		return fieldSampleId;
	}

	public void setFieldSampleId(String fieldSampleId) {
		this.fieldSampleId = fieldSampleId;
	}

	@Column
	public Integer getTurbidity() {
		return turbidity;
	}

	public void setTurbidity(Integer turbidity) {
		this.turbidity = turbidity;
	}

	@Column
	public Integer getPhValue() {
		return phValue;
	}

	public void setPhValue(Integer phValue) {
		this.phValue = phValue;
	}

	@Column
	public Integer getSampleTemperature() {
		return sampleTemperature;
	}

	public void setSampleTemperature(Integer sampleTemperature) {
		this.sampleTemperature = sampleTemperature;
	}

	@Column
	public Float getChlorineResiduals() {
		return chlorineResiduals;
	}

	public void setChlorineResiduals(Float chlorineResiduals) {
		this.chlorineResiduals = chlorineResiduals;
	}

	@ManyToOne(optional = false)
	public Facility getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(Facility laboratory) {
		this.laboratory = laboratory;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getLaboratoryDetails() {
		return laboratoryDetails;
	}

	public void setLaboratoryDetails(String laboratoryDetails) {
		this.laboratoryDetails = laboratoryDetails;
	}

	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	@Convert(converter = RequestedPathogensConverter.class)
	public Set<Pathogen> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<Pathogen> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getOtherRequestedPathogenTests() {
		return otherRequestedPathogenTests;
	}

	public void setOtherRequestedPathogenTests(String otherRequestedPathogenTests) {
		this.otherRequestedPathogenTests = otherRequestedPathogenTests;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public Map<WeatherCondition, Boolean> getWeatherConditions() {
		return weatherConditions;
	}

	public void setWeatherConditions(Map<WeatherCondition, Boolean> weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHeavyRain() {
		return heavyRain;
	}

	public void setHeavyRain(YesNoUnknown heavyRain) {
		this.heavyRain = heavyRain;
	}

	@Column
	public boolean isDispatched() {
		return dispatched;
	}

	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getDispatchDetails() {
		return dispatchDetails;
	}

	public void setDispatchDetails(String dispatchDetails) {
		this.dispatchDetails = dispatchDetails;
	}

	@Column
	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceivalDate() {
		return receivalDate;
	}

	public void setReceivalDate(Date receivalDate) {
		this.receivalDate = receivalDate;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	public Location getLocation() {
		if (location == null) {
			location = new Location();
		}

		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getGeneralComment() {
		return generalComment;
	}

	public void setGeneralComment(String generalComment) {
		this.generalComment = generalComment;
	}

	@OneToMany(mappedBy = PathogenTest.ENVIRONMENT_SAMPLE, fetch = FetchType.LAZY)
	public List<PathogenTest> getPathogenTests() {
		return pathogenTests;
	}

	public void setPathogenTests(List<PathogenTest> pathogenTests) {
		this.pathogenTests = pathogenTests;
	}
}
