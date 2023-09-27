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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EnvironmentSampleCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 3535818624154194098L;

	public static final String FREE_TEXT = "freeText";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String GPS_LAT_FROM = "gpsLatFrom";
	public static final String GPS_LAT_TO = "gpsLatTo";
	public static final String GPS_LON_FROM = "gpsLonFrom";
	public static final String GPS_LON_TO = "gpsLonTo";
	public static final String LABORATORY = "laboratory";
	public static final String TESTED_PATHOGEN = "testedPathogen";

	private EntityRelevanceStatus relevanceStatus;
	private Boolean dispatched;
	private Boolean received;
	private String freeText;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Double gpsLatFrom;
	private Double gpsLatTo;
	private Double gpsLonFrom;
	private Double gpsLonTo;
	private FacilityReferenceDto laboratory;
	private Pathogen testedPathogen;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date reportDateFrom;
	private Date reportDateTo;
	private EnvironmentReferenceDto environment;

	public EnvironmentSampleCriteria() {
		super();
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public Boolean getDispatched() {
		return dispatched;
	}

	public void setDispatched(Boolean dispatched) {
		this.dispatched = dispatched;
	}

	public Boolean getReceived() {
		return received;
	}

	public void setReceived(Boolean received) {
		this.received = received;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public EnvironmentSampleCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public EnvironmentSampleCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public Double getGpsLatFrom() {
		return gpsLatFrom;
	}

	public void setGpsLatFrom(Double gpsLatFrom) {
		this.gpsLatFrom = gpsLatFrom;
	}

	public Double getGpsLatTo() {
		return gpsLatTo;
	}

	public void setGpsLatTo(Double gpsLatTo) {
		this.gpsLatTo = gpsLatTo;
	}

	public Double getGpsLonFrom() {
		return gpsLonFrom;
	}

	public void setGpsLonFrom(Double gpsLonFrom) {
		this.gpsLonFrom = gpsLonFrom;
	}

	public Double getGpsLonTo() {
		return gpsLonTo;
	}

	public void setGpsLonTo(Double gpsLonTo) {
		this.gpsLonTo = gpsLonTo;
	}

	public FacilityReferenceDto getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(FacilityReferenceDto laboratory) {
		this.laboratory = laboratory;
	}

	public Pathogen getTestedPathogen() {
		return testedPathogen;
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public void setReportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public void setReportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
	}

	public void reportDateBetween(Date reportDateFrom, Date reportDateTo, DateFilterOption dateFilterOption) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		this.dateFilterOption = dateFilterOption;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
	}

	public EnvironmentSampleCriteria withEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
		return this;
	}
}
