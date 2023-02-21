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

package de.symeda.sormas.backend.externalmessage.labmessage;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.sample.Sample;

@Entity(name = SampleReport.TABLE_NAME)
@Audited
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class SampleReport extends AbstractDomainObject {

	private static final long serialVersionUID = -6907634648584998110L;

	public static final String TABLE_NAME = "samplereport";

	public static final String LAB_MESSAGE = "labMessage";
	public static final String SAMPLE = "sample";

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_RECEIVED_DATE = "sampleReceivedDate";
	public static final String LAB_SAMPLE_ID = "labSampleId";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String SAMPLE_OVERALL_TEST_RESULT = "sampleOverallTestResult";

	private Date sampleDateTime;
	private Date sampleReceivedDate;
	private String labSampleId;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private SpecimenCondition specimenCondition;
	private PathogenTestResultType sampleOverallTestResult;

	private List<TestReport> testReports;
	private Sample sample;

	private ExternalMessage labMessage;

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSampleReceivedDate() {
		return sampleReceivedDate;
	}

	public void setSampleReceivedDate(Date sampleReceivedDate) {
		this.sampleReceivedDate = sampleReceivedDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	@Enumerated(EnumType.STRING)
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getSampleOverallTestResult() {
		return sampleOverallTestResult;
	}

	public void setSampleOverallTestResult(PathogenTestResultType sampleOverallTestResult) {
		this.sampleOverallTestResult = sampleOverallTestResult;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = TestReport.SAMPLE_REPORT, fetch = FetchType.LAZY)
	public List<TestReport> getTestReports() {
		return testReports;
	}

	public void setTestReports(List<TestReport> testReports) {
		this.testReports = testReports;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public ExternalMessage getLabMessage() {
		return labMessage;
	}

	public void setLabMessage(ExternalMessage labMessage) {
		this.labMessage = labMessage;
	}
}
