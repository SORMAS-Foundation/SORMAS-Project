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

package de.symeda.sormas.api.externalmessage.labmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.EXTERNAL_MESSAGES)
public class SampleReportDto extends EntityDto {

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_RECEIVED_DATE = "sampleReceivedDate";
	public static final String LAB_SAMPLE_ID = "labSampleId";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String TEST_REPORTS = "testReports";

	private Date sampleDateTime;
	private Date sampleReceivedDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String labSampleId;
	private SampleMaterial sampleMaterial;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String sampleMaterialText;
	private SpecimenCondition specimenCondition;
	private PathogenTestResultType sampleOverallTestResult;

	@Valid
	private List<TestReportDto> testReports = new ArrayList<>();
	private SampleReferenceDto sample;
	@Valid
	private ExternalMessageReferenceDto labMessage;

	public static SampleReportDto build() {
		SampleReportDto sampleReport = new SampleReportDto();
		sampleReport.setUuid(DataHelper.createUuid());
		return sampleReport;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public Date getSampleReceivedDate() {
		return sampleReceivedDate;
	}

	public void setSampleReceivedDate(Date sampleReceivedDate) {
		this.sampleReceivedDate = sampleReceivedDate;
	}

	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public PathogenTestResultType getSampleOverallTestResult() {
		return sampleOverallTestResult;
	}

	public void setSampleOverallTestResult(PathogenTestResultType sampleOverallTestResult) {
		this.sampleOverallTestResult = sampleOverallTestResult;
	}

	public List<TestReportDto> getTestReports() {
		return testReports;
	}

	/**
	 * Use this method only if you want to discard already added test reports. In that case, remember to set the according reference in
	 * the test report ({@link TestReportDto#setSampleReport(SampleReportReferenceDto)}).
	 * Otherwise, use the {@link SampleReportDto#addTestReport(TestReportDto)}.
	 *
	 * @param testReports
	 */
	public void setTestReports(List<TestReportDto> testReports) {
		this.testReports = testReports;
	}

	public void addTestReport(TestReportDto testReport) {

		testReport.setSampleReport(this.toReference());
		if (this.testReports == null) {
			List<TestReportDto> testReports = new ArrayList();
			testReports.add(testReport);
			this.testReports = testReports;
		} else {
			try {
				this.testReports.add(testReport);
			} catch (UnsupportedOperationException e) {
				ArrayList<TestReportDto> newList = new ArrayList<>(this.testReports);
				newList.add(testReport);
				this.testReports = newList;
			}

		}
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	public ExternalMessageReferenceDto getLabMessage() {
		return labMessage;
	}

	public void setLabMessage(ExternalMessageReferenceDto labMessage) {
		this.labMessage = labMessage;
	}

	public SampleReportReferenceDto toReference() {
		return new SampleReportReferenceDto(getUuid());
	}

}
