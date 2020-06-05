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
import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class SampleCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -4649293670201029461L;

	public static final String PATHOGEN_TEST_RESULT = "pathogenTestResult";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String DISEASE = "disease";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String LAB = "laboratory";
	public static final String CASE_CODE_ID_LIKE = "caseCodeIdLike";

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto laboratory;
	private Boolean shipped;
	private Boolean received;
	private Boolean referred;
	private PathogenTestResultType pathogenTestResult;
	private CaseClassification caseClassification;
	private Disease disease;
	private SpecimenCondition specimenCondition;
	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private Boolean deleted = Boolean.FALSE;
	private String caseCodeIdLike;
	private EntityRelevanceStatus relevanceStatus;
	private SampleAssociationType sampleAssociationType;

	private Date sampleReportDateFrom;
	private Date sampleReportDateTo;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	
	public SampleCriteria region(RegionReferenceDto region){
		setRegion(region);

		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	public SampleCriteria district(DistrictReferenceDto district){
		setDistrict(district);

		return this;
	}

	public FacilityReferenceDto getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(FacilityReferenceDto laboratory) {
		this.laboratory = laboratory;
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

	public SampleCriteria reportDateBetween(Date reportDateFrom, Date reportDateTo, DateFilterOption dateFilterOption) {
		this.sampleReportDateFrom = reportDateFrom;
		this.sampleReportDateTo = reportDateTo;
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public SampleCriteria dateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public Date getSampleReportDateFrom() {
		return sampleReportDateFrom;
	}

	public void setSampleReportDateFrom(Date sampleReportDateFrom) {
		this.sampleReportDateFrom = sampleReportDateFrom;
	}

	public Date getSampleReportDateTo() {
		return sampleReportDateTo;
	}

	public void setSampleReportDateTo(Date sampleReportDateTo) {
		this.sampleReportDateTo = sampleReportDateTo;
	}

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}
	
	public SampleCriteria pathogenTestResult(PathogenTestResultType pathogenTestResult){
		setPathogenTestResult(pathogenTestResult);

		return this;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public SampleCriteria disease(Disease disease){
		setDisease(disease);

		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public SampleCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public SampleCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	
	public SampleCriteria specimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
		return this;
	}

	public SampleAssociationType getSampleAssociationType() {
		return sampleAssociationType;
	}

	public SampleCriteria sampleAssociationType(SampleAssociationType sampleAssociationType) {
		this.sampleAssociationType = sampleAssociationType;
		return this;
	}

	public SampleCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}
	
	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}
	
	public SampleCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	
	/**
	 * returns all entries that match ALL of the passed words
	 */
	public void setCaseCodeIdLike(String caseCodeIdLike) {
		this.caseCodeIdLike = caseCodeIdLike;
	}

	@IgnoreForUrl
	public String getCaseCodeIdLike() {
		return caseCodeIdLike;
	}
}
