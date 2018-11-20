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
package de.symeda.sormas.api.sample;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class SampleCriteria implements Serializable {

	private static final long serialVersionUID = -4649293670201029461L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto laboratory;
	private Boolean shipped;
	private Boolean received;
	private Boolean referred;
	private SampleTestResultType testResult;
	private CaseClassification caseClassification;
	private Disease disease;
	private SpecimenCondition specimenCondition;
	private CaseReferenceDto caze;
	private Boolean archived;

	public RegionReferenceDto getRegion() {
		return region;
	}

	public SampleCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public SampleCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public FacilityReferenceDto getLaboratory() {
		return laboratory;
	}

	public SampleCriteria laboratory(FacilityReferenceDto laboratory) {
		this.laboratory = laboratory;
		return this;
	}

	public Boolean getShipped() {
		return shipped;
	}

	public SampleCriteria shipped(Boolean shipped) {
		this.shipped = shipped;
		return this;
	}

	public Boolean getReceived() {
		return received;
	}

	public SampleCriteria received(Boolean received) {
		this.received = received;
		return this;
	}

	public Boolean getReferred() {
		return referred;
	}

	public SampleCriteria referred(Boolean referred) {
		this.referred = referred;
		return this;
	}

	public SampleTestResultType getTestResult() {
		return testResult;
	}

	public SampleCriteria testResult(SampleTestResultType testResult) {
		this.testResult = testResult;
		return this;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public SampleCriteria caseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public SampleCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public SampleCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public SampleCriteria specimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
		return this;
	}

	public Boolean getArchived() {
		return archived;
	}

	public SampleCriteria archived(Boolean archived) {
		this.archived = archived;
		return this;
	}
}
