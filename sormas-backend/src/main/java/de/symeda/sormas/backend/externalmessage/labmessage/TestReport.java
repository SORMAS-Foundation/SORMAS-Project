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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.sample.GenoTypeResult;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.RsvSubtype;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.country.Country;

@Entity(name = TestReport.TABLE_NAME)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class TestReport extends AbstractDomainObject {

	private static final long serialVersionUID = -9164498173635523905L;

	public static final String TABLE_NAME = "testreport";

	public static final String SAMPLE_REPORT = "sampleReport";

	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_EXTERNAL_IDS = "testLabExternalIds";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TEST_LAB_CITY = "testLabCity";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_TYPE_DETAILS = "testTypeDetails";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String TEST_RESULT = "testResult";
	public static final String DATE_OF_RESULT = "dateOfResult";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String TEST_PCR_TEST_SPECIFICATION = "testPcrTestSpecification";
	public static final String CQ_VALUE = "cqValue";
	public static final String CT_VALUE_E = "ctValueE";
	public static final String CT_VALUE_N = "ctValueN";
	public static final String CT_VALUE_RDRP = "ctValueRdrp";
	public static final String CT_VALUE_S = "ctValueS";
	public static final String CT_VALUE_ORF_1 = "ctValueOrf1";
	public static final String CT_VALUE_RDRP_S = "ctValueRdrpS";
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";

	private String testLabName;
	private List<String> testLabExternalIds;
	private String testLabPostalCode;
	private String testLabCity;

	private PathogenTestType testType;
	private String testTypeDetails;
	private Date testDateTime;
	private PathogenTestResultType testResult;
	private Date dateOfResult;
	private Boolean testResultVerified;
	private String testResultText;
	private String typingId;
	private String externalId;
	private String externalOrderId;
	private String testedDiseaseVariant;
	private String testedDiseaseVariantDetails;
	private Boolean preliminary;
	private PCRTestSpecification testPcrTestSpecification;
	private Float cqValue;
	private Float ctValueE;
	private Float ctValueN;
	private Float ctValueRdrp;
	private Float ctValueS;
	private Float ctValueOrf1;
	private Float ctValueRdrpS;

	private SampleReport sampleReport;

	private String prescriberPhysicianCode;
	private String prescriberFirstName;
	private String prescriberLastName;
	private String prescriberPhoneNumber;
	private String prescriberAddress;
	private String prescriberPostalCode;
	private String prescriberCity;
	private Country prescriberCountry;
	private GenoTypeResult genoTypeResult;
	private RsvSubtype rsvSubtype;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	@Type(type = "list-array")
	@Column(name = "testlabexternalids", columnDefinition = "VARCHAR(255) ARRAY")
	public List<String> getTestLabExternalIds() {
		return testLabExternalIds;
	}

	public void setTestLabExternalIds(List<String> testLabExternalIds) {
		this.testLabExternalIds = testLabExternalIds;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestLabPostalCode() {
		return testLabPostalCode;
	}

	public void setTestLabPostalCode(String testLabPostalCode) {
		this.testLabPostalCode = testLabPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestLabCity() {
		return testLabCity;
	}

	public void setTestLabCity(String testLabCity) {
		this.testLabCity = testLabCity;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestTypeDetails() {
		return testTypeDetails;
	}

	public void setTestTypeDetails(String testTypeDetails) {
		this.testTypeDetails = testTypeDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfResult() {
		return dateOfResult;
	}

	public void setDateOfResult(Date dateOfResult) {
		this.dateOfResult = dateOfResult;
	}

	@Column
	public Boolean isTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public String getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(String diseaseVariant) {
		this.testedDiseaseVariant = diseaseVariant;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String diseaseVariantDetails) {
		this.testedDiseaseVariantDetails = diseaseVariantDetails;
	}

	@Column
	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	@Enumerated(EnumType.STRING)
	public PCRTestSpecification getTestPcrTestSpecification() {
		return testPcrTestSpecification;
	}

	public void setTestPcrTestSpecification(PCRTestSpecification testPcrTestSpecification) {
		this.testPcrTestSpecification = testPcrTestSpecification;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public SampleReport getSampleReport() {
		return sampleReport;
	}

	public void setSampleReport(SampleReport sampleReport) {
		this.sampleReport = sampleReport;
	}

	@Column
	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	@Column
	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	@Column
	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	@Column
	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	@Column
	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	@Column
	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	@Column
	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(Country prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	@Enumerated(EnumType.STRING)
	public GenoTypeResult getGenoTypeResult() {
		return genoTypeResult;
	}

	public void setGenoTypeResult(GenoTypeResult genoTypeResult) {
		this.genoTypeResult = genoTypeResult;
	}

	@Enumerated(EnumType.STRING)
	public RsvSubtype getRsvSubtype() {
		return rsvSubtype;
	}

	public void setRsvSubtype(RsvSubtype rsvSubtype) {
		this.rsvSubtype = rsvSubtype;
	}
}
