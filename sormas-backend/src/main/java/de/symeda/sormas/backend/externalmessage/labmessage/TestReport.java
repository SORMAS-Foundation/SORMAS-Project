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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.ListArrayType;

import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.RsvSubtype;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
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

	private PathogenSpecie specie;
	private Float tubeNil;
	private Boolean tubeNilGT10;
	private Float tubeAgTb1;
	private Boolean tubeAgTb1GT10;
	private Float tubeAgTb2;
	private Boolean tubeAgTb2GT10;
	private Float tubeMitogene;
	private Boolean tubeMitogeneGT10;

	// Drug susceptibility
	private Float amikacinMic;
	private DrugSusceptibilityType amikacinSusceptibility;
	private Float bedaquilineMic;
	private DrugSusceptibilityType bedaquilineSusceptibility;
	private Float capreomycinMic;
	private DrugSusceptibilityType capreomycinSusceptibility;
	private Float ciprofloxacinMic;
	private DrugSusceptibilityType ciprofloxacinSusceptibility;
	private Float delamanidMic;
	private DrugSusceptibilityType delamanidSusceptibility;
	private Float ethambutolMic;
	private DrugSusceptibilityType ethambutolSusceptibility;
	private Float gatifloxacinMic;
	private DrugSusceptibilityType gatifloxacinSusceptibility;
	private Float isoniazidMic;
	private DrugSusceptibilityType isoniazidSusceptibility;
	private Float kanamycinMic;
	private DrugSusceptibilityType kanamycinSusceptibility;
	private Float levofloxacinMic;
	private DrugSusceptibilityType levofloxacinSusceptibility;
	private Float moxifloxacinMic;
	private DrugSusceptibilityType moxifloxacinSusceptibility;
	private Float ofloxacinMic;
	private DrugSusceptibilityType ofloxacinSusceptibility;
	private Float rifampicinMic;
	private DrugSusceptibilityType rifampicinSusceptibility;
	private Float streptomycinMic;
	private DrugSusceptibilityType streptomycinSusceptibility;
	private Float ceftriaxoneMic;
	private DrugSusceptibilityType ceftriaxoneSusceptibility;
	private Float penicillinMic;
	private DrugSusceptibilityType penicillinSusceptibility;
	private Float erythromycinMic;
	private DrugSusceptibilityType erythromycinSusceptibility;

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

	@Enumerated(EnumType.STRING)
	public PathogenSpecie getSpecie() {
		return specie;
	}

	public void setSpecie(PathogenSpecie specie) {
		this.specie = specie;
	}

	@Column
	public Float getTubeNil() {
		return tubeNil;
	}

	public void setTubeNil(Float tubeNil) {
		this.tubeNil = tubeNil;
	}

	@Column
	public Boolean getTubeNilGT10() {
		return tubeNilGT10;
	}

	public void setTubeNilGT10(Boolean tubeNilGT10) {
		this.tubeNilGT10 = tubeNilGT10;
	}

	@Column
	public Float getTubeAgTb1() {
		return tubeAgTb1;
	}

	public void setTubeAgTb1(Float tubeAgTb1) {
		this.tubeAgTb1 = tubeAgTb1;
	}

	@Column
	public Boolean getTubeAgTb1GT10() {
		return tubeAgTb1GT10;
	}

	public void setTubeAgTb1GT10(Boolean tubeAgTb1GT10) {
		this.tubeAgTb1GT10 = tubeAgTb1GT10;
	}

	@Column
	public Float getTubeAgTb2() {
		return tubeAgTb2;
	}

	public void setTubeAgTb2(Float tubeAgTb2) {
		this.tubeAgTb2 = tubeAgTb2;
	}

	@Column
	public Boolean getTubeAgTb2GT10() {
		return tubeAgTb2GT10;
	}

	public void setTubeAgTb2GT10(Boolean tubeAgTb2GT10) {
		this.tubeAgTb2GT10 = tubeAgTb2GT10;
	}

	@Column
	public Float getTubeMitogene() {
		return tubeMitogene;
	}

	public void setTubeMitogene(Float tubeMitogene) {
		this.tubeMitogene = tubeMitogene;
	}

	@Column
	public Boolean getTubeMitogeneGT10() {
		return tubeMitogeneGT10;
	}

	public void setTubeMitogeneGT10(Boolean tubeMitogeneGT10) {
		this.tubeMitogeneGT10 = tubeMitogeneGT10;
	}

	public Float getAmikacinMic() {
		return amikacinMic;
	}

	public void setAmikacinMic(Float amikacinMic) {
		this.amikacinMic = amikacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getAmikacinSusceptibility() {
		return amikacinSusceptibility;
	}

	public void setAmikacinSusceptibility(DrugSusceptibilityType amikacinSusceptibility) {
		this.amikacinSusceptibility = amikacinSusceptibility;
	}

	public Float getBedaquilineMic() {
		return bedaquilineMic;
	}

	public void setBedaquilineMic(Float bedaquilineMic) {
		this.bedaquilineMic = bedaquilineMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getBedaquilineSusceptibility() {
		return bedaquilineSusceptibility;
	}

	public void setBedaquilineSusceptibility(DrugSusceptibilityType bedaquilineSusceptibility) {
		this.bedaquilineSusceptibility = bedaquilineSusceptibility;
	}

	public Float getCapreomycinMic() {
		return capreomycinMic;
	}

	public void setCapreomycinMic(Float capreomycinMic) {
		this.capreomycinMic = capreomycinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getCapreomycinSusceptibility() {
		return capreomycinSusceptibility;
	}

	public void setCapreomycinSusceptibility(DrugSusceptibilityType capreomycinSusceptibility) {
		this.capreomycinSusceptibility = capreomycinSusceptibility;
	}

	public Float getCiprofloxacinMic() {
		return ciprofloxacinMic;
	}

	public void setCiprofloxacinMic(Float ciprofloxacinMic) {
		this.ciprofloxacinMic = ciprofloxacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getCiprofloxacinSusceptibility() {
		return ciprofloxacinSusceptibility;
	}

	public void setCiprofloxacinSusceptibility(DrugSusceptibilityType ciprofloxacinSusceptibility) {
		this.ciprofloxacinSusceptibility = ciprofloxacinSusceptibility;
	}

	public Float getDelamanidMic() {
		return delamanidMic;
	}

	public void setDelamanidMic(Float delamanidMic) {
		this.delamanidMic = delamanidMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getDelamanidSusceptibility() {
		return delamanidSusceptibility;
	}

	public void setDelamanidSusceptibility(DrugSusceptibilityType delamanidSusceptibility) {
		this.delamanidSusceptibility = delamanidSusceptibility;
	}

	public Float getEthambutolMic() {
		return ethambutolMic;
	}

	public void setEthambutolMic(Float ethambutolMic) {
		this.ethambutolMic = ethambutolMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getEthambutolSusceptibility() {
		return ethambutolSusceptibility;
	}

	public void setEthambutolSusceptibility(DrugSusceptibilityType ethambutolSusceptibility) {
		this.ethambutolSusceptibility = ethambutolSusceptibility;
	}

	public Float getGatifloxacinMic() {
		return gatifloxacinMic;
	}

	public void setGatifloxacinMic(Float gatifloxacinMic) {
		this.gatifloxacinMic = gatifloxacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getGatifloxacinSusceptibility() {
		return gatifloxacinSusceptibility;
	}

	public void setGatifloxacinSusceptibility(DrugSusceptibilityType gatifloxacinSusceptibility) {
		this.gatifloxacinSusceptibility = gatifloxacinSusceptibility;
	}

	public Float getIsoniazidMic() {
		return isoniazidMic;
	}

	public void setIsoniazidMic(Float isoniazidMic) {
		this.isoniazidMic = isoniazidMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getIsoniazidSusceptibility() {
		return isoniazidSusceptibility;
	}

	public void setIsoniazidSusceptibility(DrugSusceptibilityType isoniazidSusceptibility) {
		this.isoniazidSusceptibility = isoniazidSusceptibility;
	}

	public Float getKanamycinMic() {
		return kanamycinMic;
	}

	public void setKanamycinMic(Float kanamycinMic) {
		this.kanamycinMic = kanamycinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getKanamycinSusceptibility() {
		return kanamycinSusceptibility;
	}

	public void setKanamycinSusceptibility(DrugSusceptibilityType kanamycinSusceptibility) {
		this.kanamycinSusceptibility = kanamycinSusceptibility;
	}

	public Float getLevofloxacinMic() {
		return levofloxacinMic;
	}

	public void setLevofloxacinMic(Float levofloxacinMic) {
		this.levofloxacinMic = levofloxacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getLevofloxacinSusceptibility() {
		return levofloxacinSusceptibility;
	}

	public void setLevofloxacinSusceptibility(DrugSusceptibilityType levofloxacinSusceptibility) {
		this.levofloxacinSusceptibility = levofloxacinSusceptibility;
	}

	public Float getMoxifloxacinMic() {
		return moxifloxacinMic;
	}

	public void setMoxifloxacinMic(Float moxifloxacinMic) {
		this.moxifloxacinMic = moxifloxacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getMoxifloxacinSusceptibility() {
		return moxifloxacinSusceptibility;
	}

	public void setMoxifloxacinSusceptibility(DrugSusceptibilityType moxifloxacinSusceptibility) {
		this.moxifloxacinSusceptibility = moxifloxacinSusceptibility;
	}

	public Float getOfloxacinMic() {
		return ofloxacinMic;
	}

	public void setOfloxacinMic(Float ofloxacinMic) {
		this.ofloxacinMic = ofloxacinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getOfloxacinSusceptibility() {
		return ofloxacinSusceptibility;
	}

	public void setOfloxacinSusceptibility(DrugSusceptibilityType ofloxacinSusceptibility) {
		this.ofloxacinSusceptibility = ofloxacinSusceptibility;
	}

	public Float getRifampicinMic() {
		return rifampicinMic;
	}

	public void setRifampicinMic(Float rifampicinMic) {
		this.rifampicinMic = rifampicinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getRifampicinSusceptibility() {
		return rifampicinSusceptibility;
	}

	public void setRifampicinSusceptibility(DrugSusceptibilityType rifampicinSusceptibility) {
		this.rifampicinSusceptibility = rifampicinSusceptibility;
	}

	public Float getStreptomycinMic() {
		return streptomycinMic;
	}

	public void setStreptomycinMic(Float streptomycinMic) {
		this.streptomycinMic = streptomycinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getStreptomycinSusceptibility() {
		return streptomycinSusceptibility;
	}

	public void setStreptomycinSusceptibility(DrugSusceptibilityType streptomycinSusceptibility) {
		this.streptomycinSusceptibility = streptomycinSusceptibility;
	}

	public Float getCeftriaxoneMic() {
		return ceftriaxoneMic;
	}

	public void setCeftriaxoneMic(Float ceftriaxoneMic) {
		this.ceftriaxoneMic = ceftriaxoneMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getCeftriaxoneSusceptibility() {
		return ceftriaxoneSusceptibility;
	}

	public void setCeftriaxoneSusceptibility(DrugSusceptibilityType ceftriaxoneSusceptibility) {
		this.ceftriaxoneSusceptibility = ceftriaxoneSusceptibility;
	}

	public Float getPenicillinMic() {
		return penicillinMic;
	}

	public void setPenicillinMic(Float penicillinMic) {
		this.penicillinMic = penicillinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getPenicillinSusceptibility() {
		return penicillinSusceptibility;
	}

	public void setPenicillinSusceptibility(DrugSusceptibilityType penicillinSusceptibility) {
		this.penicillinSusceptibility = penicillinSusceptibility;
	}

	public Float getErythromycinMic() {
		return erythromycinMic;
	}

	public void setErythromycinMic(Float erythromycinMic) {
		this.erythromycinMic = erythromycinMic;
	}

	@Enumerated(EnumType.STRING)
	public DrugSusceptibilityType getErythromycinSusceptibility() {
		return erythromycinSusceptibility;
	}

	public void setErythromycinSusceptibility(DrugSusceptibilityType erythromycinSusceptibility) {
		this.erythromycinSusceptibility = erythromycinSusceptibility;
	}

}
