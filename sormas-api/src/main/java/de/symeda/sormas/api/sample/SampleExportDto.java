package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Order;

public class SampleExportDto implements Serializable {

	private static final long serialVersionUID = -3027326087594387560L;

	public static final String I18N_PREFIX = "SampleExport";

	private long id;
	private String uuid;
	private String labSampleID;
	private String epidNumber;
	private String firstName;
	private String lastName;
	private String disease;
	private Date sampleDateTime;
	private String sampleMaterial;
	private SampleSource sampleSource;
	private String lab;
	private PathogenTestResultType pathogenTestResult;
	private Boolean pathogenTestingRequested;
	private Set<PathogenTestType> requestedPathogenTests;
	private String requestedOtherPathogenTests;
	private Boolean additionalTestingRequested;
	private Set<AdditionalTestType> requestedAdditionalTests;
	private String requestedOtherAdditionalTests;
	private boolean shipped;
	private Date shipmentDate;
	private String shipmentDetails;
	private boolean received;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private String referredToUuid;
	private String caseUuid;
	private String caseAge;
	private Sex caseSex;
	private long caseAddressId;
	private String caseAddress;
	private Date caseReportDate;
	private CaseClassification caseClassification;
	private CaseOutcome caseOutcome;
	private String caseRegion;
	private String caseDistrict;
	private String caseCommunity;
	private String caseFacility;
	private String pathogenTestType1;
	private String pathogenTestDisease1;
	private Date pathogenTestDateTime1;
	private String pathogenTestLab1;
	private PathogenTestResultType pathogenTestResult1;
	private Boolean pathogenTestVerified1;
	private String pathogenTestType2;
	private String pathogenTestDisease2;
	private Date pathogenTestDateTime2;
	private String pathogenTestLab2;
	private PathogenTestResultType pathogenTestResult2;
	private Boolean pathogenTestVerified2;
	private String pathogenTestType3;
	private String pathogenTestDisease3;
	private Date pathogenTestDateTime3;
	private String pathogenTestLab3;
	private PathogenTestResultType pathogenTestResult3;
	private Boolean pathogenTestVerified3;
	private String otherPathogenTestsDetails = "";
	private AdditionalTestDto additionalTest;
	private String otherAdditionalTestsDetails = "";

	public SampleExportDto(long id, String uuid, String labSampleId, String epidNumber, String firstName, String lastName, 
			Disease disease, String diseaseDetails, Date sampleDateTime, SampleMaterial sampleMaterial, String sampleMaterialDetails,
			SampleSource sampleSource, String laboratoryUuid, String laboratory, String laboratoryDetails,
			PathogenTestResultType pathogenTestResult, Boolean pathogenTestingRequested, String requestedPathogenTests, String requestedOtherPathogenTests, 
			Boolean additionalTestingRequested, String requestedAdditionalTests, String requestedOtherAdditionalTests, boolean shipped, Date shipmentDate,
			String shipmentDetails, boolean received, Date receivedDate, SpecimenCondition specimenCondition,
			String noTestPossibleReason, String comment, String referredToUuid, String caseUuid, Integer approximateAge,
			ApproximateAgeType approximateAgeType, Sex caseSex, long caseAddressId, Date caseReportDate,
			CaseClassification caseClassification, CaseOutcome caseOutcome, String caseRegion, String caseDistrict,
			String caseCommunity, String caseFacilityUuid, String caseFacility, String caseFacilityDetails) {
		this.id = id;
		this.uuid = uuid;
		this.labSampleID = labSampleId;
		this.epidNumber = epidNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.disease = DiseaseHelper.toString(disease, diseaseDetails);
		this.sampleDateTime = sampleDateTime;
		this.sampleMaterial = SampleMaterial.toString(sampleMaterial, sampleMaterialDetails);
		this.sampleSource = sampleSource;
		this.lab = FacilityHelper.buildFacilityString(laboratoryUuid, laboratory, laboratoryDetails);
		this.pathogenTestResult = pathogenTestResult;
		this.pathogenTestingRequested = pathogenTestingRequested;
		this.requestedPathogenTests = new HashSet<>();
		if (!StringUtils.isEmpty(requestedPathogenTests)) {
			for (String s : requestedPathogenTests.split(",")) {
				this.requestedPathogenTests.add(PathogenTestType.valueOf(s));
			}
		}
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
		this.additionalTestingRequested = additionalTestingRequested;
		this.requestedAdditionalTests = new HashSet<>();
		if (!StringUtils.isEmpty(requestedAdditionalTests)) {
			for (String s : requestedAdditionalTests.split(",")) {
				this.requestedAdditionalTests.add(AdditionalTestType.valueOf(s));
			}
		}
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
		this.shipped = shipped;
		this.shipmentDate = shipmentDate;
		this.shipmentDetails = shipmentDetails;
		this.received = received;
		this.receivedDate = receivedDate;
		this.specimenCondition = specimenCondition;
		this.noTestPossibleReason = noTestPossibleReason;
		this.comment = comment;
		this.referredToUuid = referredToUuid;
		this.caseUuid = caseUuid;
		this.caseAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.caseSex = caseSex;
		this.caseAddressId = caseAddressId;
		this.caseReportDate = caseReportDate;
		this.caseClassification = caseClassification;
		this.caseOutcome = caseOutcome;
		this.caseRegion = caseRegion;
		this.caseDistrict = caseDistrict;
		this.caseCommunity = caseCommunity;
		this.caseFacility = FacilityHelper.buildFacilityString(caseFacilityUuid, caseFacility, caseFacilityDetails);
	}

	@Order(0)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Order(1)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Order(2)
	public String getLabSampleID() {
		return labSampleID;
	}

	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}

	@Order(3)
	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	@Order(4)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Order(5)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Order(6)
	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	@Order(10)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Order(11)
	public String getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(String sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Order(12)
	public SampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	@Order(13)
	public String getLab() {
		return lab;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	@Order(14)
	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	@Order(15)
	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	@Order(16)
	public Set<PathogenTestType> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	@Order(17)
	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	@Order(20)
	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	@Order(21)
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;
	}

	@Order(22)
	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
	}

	@Order(23)
	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	@Order(24)
	public Date getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Order(25)
	public String getShipmentDetails() {
		return shipmentDetails;
	}

	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}

	@Order(26)
	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	@Order(27)
	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Order(30)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Order(31)
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}

	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}

	@Order(32)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Order(33)
	public String getReferredToUuid() {
		return referredToUuid;
	}

	public void setReferredToUuid(String referredToUuid) {
		this.referredToUuid = referredToUuid;
	}

	@Order(34)
	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	@Order(35)
	public String getCaseAge() {
		return caseAge;
	}

	public void setCaseAge(String caseAge) {
		this.caseAge = caseAge;
	}

	@Order(36)
	public Sex getCaseSex() {
		return caseSex;
	}

	public void setCaseSex(Sex caseSex) {
		this.caseSex = caseSex;
	}

	@Order(41)
	public String getCaseAddress() {
		return caseAddress;
	}

	public void setCaseAddress(String caseAddress) {
		this.caseAddress = caseAddress;
	}

	@Order(42)
	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}

	@Order(43)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	@Order(44)
	public CaseOutcome getCaseOutcome() {
		return caseOutcome;
	}

	public void setCaseOutcome(CaseOutcome caseOutcome) {
		this.caseOutcome = caseOutcome;
	}

	@Order(45)
	public String getCaseRegion() {
		return caseRegion;
	}

	public void setCaseRegion(String caseRegion) {
		this.caseRegion = caseRegion;
	}

	@Order(46)
	public String getCaseDistrict() {
		return caseDistrict;
	}

	public void setCaseDistrict(String caseDistrict) {
		this.caseDistrict = caseDistrict;
	}

	@Order(47)
	public String getCaseCommunity() {
		return caseCommunity;
	}

	public void setCaseCommunity(String caseCommunity) {
		this.caseCommunity = caseCommunity;
	}

	@Order(48)
	public String getCaseFacility() {
		return caseFacility;
	}

	public void setCaseFacility(String caseFacility) {
		this.caseFacility = caseFacility;
	}

	@Order(50)
	public String getPathogenTestType1() {
		return pathogenTestType1;
	}

	public void setPathogenTestType1(String pathogenTestType1) {
		this.pathogenTestType1 = pathogenTestType1;
	}

	@Order(51)
	public String getPathogenTestDisease1() {
		return pathogenTestDisease1;
	}

	public void setPathogenTestDisease1(String pathogenTestDisease1) {
		this.pathogenTestDisease1 = pathogenTestDisease1;
	}

	@Order(52)
	public Date getPathogenTestDateTime1() {
		return pathogenTestDateTime1;
	}

	public void setPathogenTestDateTime1(Date pathogenTestDateTime1) {
		this.pathogenTestDateTime1 = pathogenTestDateTime1;
	}

	@Order(53)
	public String getPathogenTestLab1() {
		return pathogenTestLab1;
	}

	public void setPathogenTestLab1(String pathogenTestLab1) {
		this.pathogenTestLab1 = pathogenTestLab1;
	}

	@Order(54)
	public PathogenTestResultType getPathogenTestResult1() {
		return pathogenTestResult1;
	}

	public void setPathogenTestResult1(PathogenTestResultType pathogenTestResult1) {
		this.pathogenTestResult1 = pathogenTestResult1;
	}

	@Order(55)
	public Boolean getPathogenTestVerified1() {
		return pathogenTestVerified1;
	}

	public void setPathogenTestVerified1(Boolean pathogenTestVerified1) {
		this.pathogenTestVerified1 = pathogenTestVerified1;
	}

	@Order(60)
	public String getPathogenTestType2() {
		return pathogenTestType2;
	}

	public void setPathogenTestType2(String pathogenTestType2) {
		this.pathogenTestType2 = pathogenTestType2;
	}

	@Order(61)
	public String getPathogenTestDisease2() {
		return pathogenTestDisease2;
	}

	public void setPathogenTestDisease2(String pathogenTestDisease2) {
		this.pathogenTestDisease2 = pathogenTestDisease2;
	}

	@Order(62)
	public Date getPathogenTestDateTime2() {
		return pathogenTestDateTime2;
	}

	public void setPathogenTestDateTime2(Date pathogenTestDateTime2) {
		this.pathogenTestDateTime2 = pathogenTestDateTime2;
	}

	@Order(63)
	public String getPathogenTestLab2() {
		return pathogenTestLab2;
	}

	public void setPathogenTestLab2(String pathogenTestLab2) {
		this.pathogenTestLab2 = pathogenTestLab2;
	}

	@Order(64)
	public PathogenTestResultType getPathogenTestResult2() {
		return pathogenTestResult2;
	}

	public void setPathogenTestResult2(PathogenTestResultType pathogenTestResult2) {
		this.pathogenTestResult2 = pathogenTestResult2;
	}

	@Order(65)
	public Boolean getPathogenTestVerified2() {
		return pathogenTestVerified2;
	}

	public void setPathogenTestVerified2(Boolean pathogenTestVerified2) {
		this.pathogenTestVerified2 = pathogenTestVerified2;
	}

	@Order(70)
	public String getPathogenTestType3() {
		return pathogenTestType3;
	}

	public void setPathogenTestType3(String pathogenTestType3) {
		this.pathogenTestType3 = pathogenTestType3;
	}

	@Order(71)
	public String getPathogenTestDisease3() {
		return pathogenTestDisease3;
	}

	public void setPathogenTestDisease3(String pathogenTestDisease3) {
		this.pathogenTestDisease3 = pathogenTestDisease3;
	}

	@Order(72)
	public Date getPathogenTestDateTime3() {
		return pathogenTestDateTime3;
	}

	public void setPathogenTestDateTime3(Date pathogenTestDateTime3) {
		this.pathogenTestDateTime3 = pathogenTestDateTime3;
	}

	@Order(73)
	public String getPathogenTestLab3() {
		return pathogenTestLab3;
	}

	public void setPathogenTestLab3(String pathogenTestLab3) {
		this.pathogenTestLab3 = pathogenTestLab3;
	}

	@Order(74)
	public PathogenTestResultType getPathogenTestResult3() {
		return pathogenTestResult3;
	}

	public void setPathogenTestResult3(PathogenTestResultType pathogenTestResult3) {
		this.pathogenTestResult3 = pathogenTestResult3;
	}

	@Order(75)
	public Boolean getPathogenTestVerified3() {
		return pathogenTestVerified3;
	}

	public void setPathogenTestVerified3(Boolean pathogenTestVerified3) {
		this.pathogenTestVerified3 = pathogenTestVerified3;
	}

	@Order(76)
	public String getOtherPathogenTestsDetails() {
		return otherPathogenTestsDetails;
	}

	public void setOtherPathogenTestsDetails(String otherPathogenTestsDetails) {
		this.otherPathogenTestsDetails = otherPathogenTestsDetails;
	}

	@Order(80)
	public AdditionalTestDto getAdditionalTest() {
		return additionalTest;
	}

	public void setAdditionalTest(AdditionalTestDto additionalTest) {
		this.additionalTest = additionalTest;
	}

	@Order(81)
	public String getOtherAdditionalTestsDetails() {
		return otherAdditionalTestsDetails;
	}

	public void setOtherAdditionalTestsDetails(String otherAdditionalTestsDetails) {
		this.otherAdditionalTestsDetails = otherAdditionalTestsDetails;
	}

	public long getCaseAddressId() {
		return caseAddressId;
	}

	public void setCaseAddressId(long caseAddressId) {
		this.caseAddressId = caseAddressId;
	}
	
}
