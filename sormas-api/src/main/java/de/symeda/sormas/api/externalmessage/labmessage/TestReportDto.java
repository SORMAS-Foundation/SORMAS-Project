package de.symeda.sormas.api.externalmessage.labmessage;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.RsvSubtype;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.EXTERNAL_MESSAGES)
public class TestReportDto extends EntityDto {

	private static final long serialVersionUID = 3377642632219354380L;

	public static final String I18N_PREFIX = "TestReport";

	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_EXTERNAL_ID = "testLabExternalId";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TEST_LAB_CITY = "testLabCity";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_TYPE_DETAILS = "testTypeDetails";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String TEST_RESULT = "testResult";
	public static final String DATE_OF_RESULT = "dateOfResult";
	public static final String TEST_PCR_TEST_SPECIFICATION = "testPcrTestSpecification";
	public static final String CQ_VALUE = "cqValue";
	public static final String CT_VALUE_E = "ctValueE";
	public static final String CT_VALUE_N = "ctValueN";
	public static final String CT_VALUE_RDRP = "ctValueRdrp";
	public static final String CT_VALUE_S = "ctValueS";
	public static final String CT_VALUE_ORF_1 = "ctValueOrf1";
	public static final String CT_VALUE_RDRP_S = "ctValueRdrpS";
	public static final String SPECIE = "specie";
	public static final String TUBE_NIL = "tubeNil";
	public static final String TUBE_NIL_GT10 = "tubeNilGT10";
	public static final String TUBE_AG_TB1 = "tubeAgTb1";
	public static final String TUBE_AG_TB1_GT10 = "tubeAgTb1GT10";
	public static final String TUBE_AG_TB2 = "tubeAgTb2";
	public static final String TUBE_AG_TB2_GT10 = "tubeAgTb2GT10";
	public static final String TUBE_MITOGENE = "tubeMitogene";
	public static final String TUBE_MITOGENE_GT10 = "tubeMitogeneGT10";
	public static final String STRAIN_CALL_STATUS = "strainCallStatus";
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";

	@NotNull(message = Validations.requiredField)
	private SampleReportReferenceDto sampleReport;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String testLabName;
	private List<String> testLabExternalIds;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String testLabPostalCode;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String testLabCity;

	private PathogenTestType testType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String testTypeDetails;
	private Date testDateTime;
	private PathogenTestResultType testResult;
	private Date dateOfResult;
	private Boolean testResultVerified;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String testResultText;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String typingId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalOrderId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String testedDiseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String testedDiseaseVariantDetails;

	private Boolean preliminary;
	private PCRTestSpecification testPcrTestSpecification;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float cqValue;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueE;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueN;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrp;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueS;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueOrf1;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrpS;
	private PathogenSpecie specie;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float tubeNil;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Boolean tubeNilGT10;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float tubeAgTb1;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Boolean tubeAgTb1GT10;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float tubeAgTb2;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Boolean tubeAgTb2GT10;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float tubeMitogene;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Boolean tubeMitogeneGT10;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private PathogenStrainCallStatus strainCallStatus;

	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhysicianCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberFirstName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberLastName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhoneNumber;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberAddress;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPostalCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberCity;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private CountryReferenceDto prescriberCountry;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private GenoTypeResult genoTypeResult;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private RsvSubtype rsvSubtype;

	// Drug susceptibility fields
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

	private SeroGroupSpecification seroGroupSpecification;
	private String seroGroupSpecificationText;
	private SerotypingMethod seroTypingMethod;
	private String seroTypingMethodText;
	private String serotype;

	public SampleReportReferenceDto getSampleReport() {
		return sampleReport;
	}

	public void setSampleReport(SampleReportReferenceDto sampleReport) {
		this.sampleReport = sampleReport;
	}

	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	public List<String> getTestLabExternalIds() {
		return testLabExternalIds;
	}

	public void setTestLabExternalIds(List<String> testLabExternalIds) {
		this.testLabExternalIds = testLabExternalIds;
	}

	public String getTestLabPostalCode() {
		return testLabPostalCode;
	}

	public void setTestLabPostalCode(String testLabPostalCode) {
		this.testLabPostalCode = testLabPostalCode;
	}

	public String getTestLabCity() {
		return testLabCity;
	}

	public void setTestLabCity(String testLabCity) {
		this.testLabCity = testLabCity;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public String getTestTypeDetails() {
		return testTypeDetails;
	}

	public void setTestTypeDetails(String testTypeDetails) {
		this.testTypeDetails = testTypeDetails;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public Date getDateOfResult() {
		return dateOfResult;
	}

	public void setDateOfResult(Date dateOfResult) {
		this.dateOfResult = dateOfResult;
	}

	public Boolean isTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public static TestReportDto build() {
		TestReportDto testResult = new TestReportDto();
		testResult.setUuid(DataHelper.createUuid());
		return testResult;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public String getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(String testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	public PCRTestSpecification getTestPcrTestSpecification() {
		return testPcrTestSpecification;
	}

	public void setTestPcrTestSpecification(PCRTestSpecification testPcrTestSpecification) {
		this.testPcrTestSpecification = testPcrTestSpecification;
	}

	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	public PathogenSpecie getSpecie() {
		return specie;
	}

	public void setSpecie(PathogenSpecie specie) {
		this.specie = specie;
	}

	public Float getTubeNil() {
		return tubeNil;
	}

	public void setTubeNil(Float tubeNil) {
		this.tubeNil = tubeNil;
	}

	public Boolean getTubeNilGT10() {
		return tubeNilGT10;
	}

	public void setTubeNilGT10(Boolean tubeNilGT10) {
		this.tubeNilGT10 = tubeNilGT10;
	}

	public Float getTubeAgTb1() {
		return tubeAgTb1;
	}

	public void setTubeAgTb1(Float tubeAgTb1) {
		this.tubeAgTb1 = tubeAgTb1;
	}

	public Boolean getTubeAgTb1GT10() {
		return tubeAgTb1GT10;
	}

	public void setTubeAgTb1GT10(Boolean tubeAgTb1GT10) {
		this.tubeAgTb1GT10 = tubeAgTb1GT10;
	}

	public Float getTubeAgTb2() {
		return tubeAgTb2;
	}

	public void setTubeAgTb2(Float tubeAgTb2) {
		this.tubeAgTb2 = tubeAgTb2;
	}

	public Boolean getTubeAgTb2GT10() {
		return tubeAgTb2GT10;
	}

	public void setTubeAgTb2GT10(Boolean tubeAgTb2GT10) {
		this.tubeAgTb2GT10 = tubeAgTb2GT10;
	}

	public Float getTubeMitogene() {
		return tubeMitogene;
	}

	public void setTubeMitogene(Float tubeMitogene) {
		this.tubeMitogene = tubeMitogene;
	}

	public Boolean getTubeMitogeneGT10() {
		return tubeMitogeneGT10;
	}

	public void setTubeMitogeneGT10(Boolean tubeMitogeneGT10) {
		this.tubeMitogeneGT10 = tubeMitogeneGT10;
	}

	public PathogenStrainCallStatus getStrainCallStatus() {
		return strainCallStatus;
	}

	public void setStrainCallStatus(PathogenStrainCallStatus strainCallStatus) {
		this.strainCallStatus = strainCallStatus;
	}

	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	public CountryReferenceDto getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(CountryReferenceDto prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	public GenoTypeResult getGenoTypeResult() {
		return genoTypeResult;
	}

	public void setGenoTypeResult(GenoTypeResult genoTypeResult) {
		this.genoTypeResult = genoTypeResult;
	}

	public RsvSubtype getRsvSubtype() {
		return rsvSubtype;
	}

	public void setRsvSubtype(RsvSubtype rsvSubtype) {
		this.rsvSubtype = rsvSubtype;
	}

	// Drug susceptibility getters and setters
	public Float getAmikacinMic() {
		return amikacinMic;
	}

	public void setAmikacinMic(Float amikacinMic) {
		this.amikacinMic = amikacinMic;
	}

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

	public DrugSusceptibilityType getErythromycinSusceptibility() {
		return erythromycinSusceptibility;
	}

	public void setErythromycinSusceptibility(DrugSusceptibilityType erythromycinSusceptibility) {
		this.erythromycinSusceptibility = erythromycinSusceptibility;
	}

	public SeroGroupSpecification getSeroGroupSpecification() {
		return seroGroupSpecification;
	}

	public void setSeroGroupSpecification(SeroGroupSpecification seroGroupSpecification) {
		this.seroGroupSpecification = seroGroupSpecification;
	}

	public String getSeroGroupSpecificationText() {
		return seroGroupSpecificationText;
	}

	public void setSeroGroupSpecificationText(String seroGroupSpecificationText) {
		this.seroGroupSpecificationText = seroGroupSpecificationText;
	}

	public SerotypingMethod getSeroTypingMethod() {
		return seroTypingMethod;
	}

	public void setSeroTypingMethod(SerotypingMethod seroTypingMethod) {
		this.seroTypingMethod = seroTypingMethod;
	}

	public String getSeroTypingMethodText() {
		return seroTypingMethodText;
	}

	public void setSeroTypingMethodText(String seroTypingMethodText) {
		this.seroTypingMethodText = seroTypingMethodText;
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}
}
