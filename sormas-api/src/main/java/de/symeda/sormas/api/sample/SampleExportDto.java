package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SampleExportDto implements Serializable {

	private static final long serialVersionUID = -3027326087594387560L;

	public static final String I18N_PREFIX = "SampleExport";

	private long id;
	private String uuid;
	private String labSampleID;
	private String epidNumber;
	private AssociatedCase associatedCase;
	private ContactReferenceDto associatedContact;
	private String contactRegion;
	private String contactDistrict;
	private String disease;
	private Date sampleDateTime;
	private String sampleMaterial;
	private String samplePurpose;
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
	private String contactUuid;
	private String personAge;
	private Sex personSex;
	private SampleExportPersonAddress personAddress;
	private Date caseReportDate;
	private CaseClassification caseClassification;
	private CaseOutcome caseOutcome;
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
	private Date contactReportDate;
	private Date lastContactDate;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;

	private CaseJurisdictionDto associatedCaseJurisdiction;
	private ContactJurisdictionDto associatedContactJurisdiction;

	//@formatter:off
	public SampleExportDto(long id, String uuid, String labSampleId, String epidNumber, String casePersonFirstName, String casePersonLastName, String contactPersonFirstName, String contactPersonLastName, 
						   Disease caseDisease, String caseDiseaseDetails, Disease contactDisease, String contactDiseaseDetails,
						   Date sampleDateTime, SampleMaterial sampleMaterial, String sampleMaterialDetails, SamplePurpose samplePurpose,
						   SampleSource sampleSource, String laboratoryUuid, String laboratory, String laboratoryDetails,
						   PathogenTestResultType pathogenTestResult, Boolean pathogenTestingRequested, String requestedPathogenTests, String requestedOtherPathogenTests,
						   Boolean additionalTestingRequested, String requestedAdditionalTests, String requestedOtherAdditionalTests, boolean shipped, Date shipmentDate,
						   String shipmentDetails, boolean received, Date receivedDate, SpecimenCondition specimenCondition,
						   String noTestPossibleReason, String comment, String referredToUuid, String caseUuid, String contactUuid,
						   Integer casePersonApproximateAge, ApproximateAgeType casePersonApproximateAgeType, Sex casePersonSex,
						   Integer contactPersonApproximateAge, ApproximateAgeType contactPersonApproximateAgeType, Sex contactPersonSex,
						   String caseAddressRegion, String caseAddressDistrict, String caseAddressCommunity, String caseAddressCity, String caseAddressAddress,
						   String contactAddressRegion, String contactAddressDistrict, String contactAddressCommunity, String contactAddressCity, String contactAddressAddress,
						   Date caseReportDate, CaseClassification caseClassification, CaseOutcome caseOutcome, String caseRegion, String caseDistrict,
						   String caseCommunity, String caseHealthFacility, String caseFacilityDetails, String contactRegion, String contactDistrict,
						   Date contactReportDate, Date lastContactDate, ContactClassification contactClassification, ContactStatus contactStatus,
						   String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUuid, String caseHealthFacilityUuid, String casePointOfEntryUuid,
						   String contactReportingUserUuid, String contactRegionUuid, String contactDistrictUuid,
						   String contactCaseReportingUserUuid, String contactCaseRegionUuid, String contactCaseDistrictUuid, String contactCaseCommunityUuid, String contactCaseHealthFacilityUuid, String contactCasePointOfEntryUuid
	) {
	//@formatter:on

		this.id = id;
		this.uuid = uuid;
		this.labSampleID = labSampleId;
		this.epidNumber = epidNumber;

		if (caseUuid != null) {
			this.associatedCase = new AssociatedCase(
				caseUuid,
				casePersonFirstName,
				casePersonLastName,
				caseRegion,
				caseDistrict,
				caseCommunity,
				caseHealthFacilityUuid,
				caseHealthFacility,
				caseFacilityDetails);
			this.associatedCaseJurisdiction = new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUuid,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		}
		if (contactUuid != null) {
			this.associatedContact = new ContactReferenceDto(contactUuid, contactPersonFirstName, contactPersonLastName, null, null);
			CaseJurisdictionDto contactCaseJurisdiction = contactCaseReportingUserUuid == null
				? null
				: new CaseJurisdictionDto(
					contactCaseReportingUserUuid,
					contactCaseRegionUuid,
					contactCaseDistrictUuid,
					contactCaseCommunityUuid,
					contactCaseHealthFacilityUuid,
					contactCasePointOfEntryUuid);
			this.associatedContactJurisdiction =
				new ContactJurisdictionDto(contactReportingUserUuid, contactRegionUuid, contactDistrictUuid, contactCaseJurisdiction);
			this.contactRegion = contactRegion;
			this.contactDistrict = contactDistrict;
		}

		this.disease = caseUuid != null
			? DiseaseHelper.toString(caseDisease, caseDiseaseDetails)
			: DiseaseHelper.toString(contactDisease, contactDiseaseDetails);
		this.sampleDateTime = sampleDateTime;
		this.sampleMaterial = SampleMaterial.toString(sampleMaterial, sampleMaterialDetails);
		if (samplePurpose != null)
			this.samplePurpose = samplePurpose.toString();
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
		this.contactUuid = contactUuid;
		this.personAge = caseUuid != null
			? ApproximateAgeHelper.formatApproximateAge(casePersonApproximateAge, casePersonApproximateAgeType)
			: ApproximateAgeHelper.formatApproximateAge(contactPersonApproximateAge, contactPersonApproximateAgeType);
		this.personSex = caseUuid != null ? casePersonSex : contactPersonSex;
		this.personAddress = caseUuid != null
			? new SampleExportPersonAddress(caseAddressRegion, caseAddressDistrict, caseAddressCommunity, caseAddressCity, caseAddressAddress)
			: new SampleExportPersonAddress(
				contactAddressRegion,
				contactAddressDistrict,
				contactAddressCommunity,
				contactAddressCity,
				contactAddressAddress);
		this.caseReportDate = caseReportDate;
		this.caseClassification = caseClassification;
		this.caseOutcome = caseOutcome;
		this.contactReportDate = contactReportDate;
		this.lastContactDate = lastContactDate;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
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

	public AssociatedCase getAssociatedCase() {
		return associatedCase;
	}

	public ContactReferenceDto getAssociatedContact() {
		return associatedContact;
	}

	@Order(4)
	public String getFirstName() {
		return associatedCase != null ? associatedCase.getFirstName() : associatedContact.getContactName().getFirstName();
	}

	@Order(5)
	public String getLastName() {
		return associatedCase != null ? associatedCase.getLastName() : associatedContact.getContactName().getLastName();
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
	public String getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(String samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	@Order(13)
	public SampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	@Order(14)
	public String getLab() {
		return lab;
	}

	public void setLab(String lab) {
		this.lab = lab;
	}

	@Order(15)
	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	@Order(16)
	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	@Order(17)
	public Set<PathogenTestType> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	@Order(18)
	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	@Order(21)
	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	@Order(22)
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;
	}

	@Order(23)
	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
	}

	@Order(24)
	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	@Order(25)
	public Date getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	@Order(26)
	public String getShipmentDetails() {
		return shipmentDetails;
	}

	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}

	@Order(27)
	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	@Order(28)
	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Order(31)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Order(32)
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}

	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}

	@Order(33)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Order(34)
	public String getReferredToUuid() {
		return referredToUuid;
	}

	public void setReferredToUuid(String referredToUuid) {
		this.referredToUuid = referredToUuid;
	}

	@Order(40)
	public String getPersonAddressCaption() {
		return LocationReferenceDto
			.buildCaption(personAddress.region, personAddress.district, personAddress.community, personAddress.city, personAddress.address);
	}

	public SampleExportPersonAddress getPersonAddress() {
		return personAddress;
	}

	@Order(41)
	public String getPersonAge() {
		return personAge;
	}

	public void setPersonAge(String personAge) {
		this.personAge = personAge;
	}

	@Order(42)
	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	@Order(50)
	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	@Order(51)
	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}

	@Order(52)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	@Order(53)
	public CaseOutcome getCaseOutcome() {
		return caseOutcome;
	}

	public void setCaseOutcome(CaseOutcome caseOutcome) {
		this.caseOutcome = caseOutcome;
	}

	@Order(54)
	public String getCaseRegion() {
		return associatedCase != null ? associatedCase.getRegion() : null;
	}

	@Order(55)
	public String getCaseDistrict() {
		return associatedCase != null ? associatedCase.getDistrict() : null;
	}

	@Order(56)
	public String getCaseCommunity() {
		return associatedCase != null ? associatedCase.getCommunity() : null;
	}

	@Order(57)
	public String getCaseFacility() {
		return associatedCase != null ? associatedCase.getFacility() : null;
	}

	@Order(60)
	public String getContactUuid() {
		return contactUuid;
	}

	@Order(61)
	public Date getContactReportDate() {
		return contactReportDate;
	}

	@Order(62)
	public Date getLastContactDate() {
		return lastContactDate;
	}

	@Order(63)
	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	@Order(64)
	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	@Order(65)
	public String getContactRegion() {
		return contactRegion;
	}

	@Order(66)
	public String getContactDistrict() {
		return contactDistrict;
	}

	@Order(71)
	public String getPathogenTestType1() {
		return pathogenTestType1;
	}

	public void setPathogenTestType1(String pathogenTestType1) {
		this.pathogenTestType1 = pathogenTestType1;
	}

	@Order(72)
	public String getPathogenTestDisease1() {
		return pathogenTestDisease1;
	}

	public void setPathogenTestDisease1(String pathogenTestDisease1) {
		this.pathogenTestDisease1 = pathogenTestDisease1;
	}

	@Order(73)
	public Date getPathogenTestDateTime1() {
		return pathogenTestDateTime1;
	}

	public void setPathogenTestDateTime1(Date pathogenTestDateTime1) {
		this.pathogenTestDateTime1 = pathogenTestDateTime1;
	}

	@Order(74)
	public String getPathogenTestLab1() {
		return pathogenTestLab1;
	}

	public void setPathogenTestLab1(String pathogenTestLab1) {
		this.pathogenTestLab1 = pathogenTestLab1;
	}

	@Order(75)
	public PathogenTestResultType getPathogenTestResult1() {
		return pathogenTestResult1;
	}

	public void setPathogenTestResult1(PathogenTestResultType pathogenTestResult1) {
		this.pathogenTestResult1 = pathogenTestResult1;
	}

	@Order(76)
	public Boolean getPathogenTestVerified1() {
		return pathogenTestVerified1;
	}

	public void setPathogenTestVerified1(Boolean pathogenTestVerified1) {
		this.pathogenTestVerified1 = pathogenTestVerified1;
	}

	@Order(81)
	public String getPathogenTestType2() {
		return pathogenTestType2;
	}

	public void setPathogenTestType2(String pathogenTestType2) {
		this.pathogenTestType2 = pathogenTestType2;
	}

	@Order(82)
	public String getPathogenTestDisease2() {
		return pathogenTestDisease2;
	}

	public void setPathogenTestDisease2(String pathogenTestDisease2) {
		this.pathogenTestDisease2 = pathogenTestDisease2;
	}

	@Order(83)
	public Date getPathogenTestDateTime2() {
		return pathogenTestDateTime2;
	}

	public void setPathogenTestDateTime2(Date pathogenTestDateTime2) {
		this.pathogenTestDateTime2 = pathogenTestDateTime2;
	}

	@Order(84)
	public String getPathogenTestLab2() {
		return pathogenTestLab2;
	}

	public void setPathogenTestLab2(String pathogenTestLab2) {
		this.pathogenTestLab2 = pathogenTestLab2;
	}

	@Order(85)
	public PathogenTestResultType getPathogenTestResult2() {
		return pathogenTestResult2;
	}

	public void setPathogenTestResult2(PathogenTestResultType pathogenTestResult2) {
		this.pathogenTestResult2 = pathogenTestResult2;
	}

	@Order(86)
	public Boolean getPathogenTestVerified2() {
		return pathogenTestVerified2;
	}

	public void setPathogenTestVerified2(Boolean pathogenTestVerified2) {
		this.pathogenTestVerified2 = pathogenTestVerified2;
	}

	@Order(91)
	public String getPathogenTestType3() {
		return pathogenTestType3;
	}

	public void setPathogenTestType3(String pathogenTestType3) {
		this.pathogenTestType3 = pathogenTestType3;
	}

	@Order(92)
	public String getPathogenTestDisease3() {
		return pathogenTestDisease3;
	}

	public void setPathogenTestDisease3(String pathogenTestDisease3) {
		this.pathogenTestDisease3 = pathogenTestDisease3;
	}

	@Order(93)
	public Date getPathogenTestDateTime3() {
		return pathogenTestDateTime3;
	}

	public void setPathogenTestDateTime3(Date pathogenTestDateTime3) {
		this.pathogenTestDateTime3 = pathogenTestDateTime3;
	}

	@Order(94)
	public String getPathogenTestLab3() {
		return pathogenTestLab3;
	}

	public void setPathogenTestLab3(String pathogenTestLab3) {
		this.pathogenTestLab3 = pathogenTestLab3;
	}

	@Order(95)
	public PathogenTestResultType getPathogenTestResult3() {
		return pathogenTestResult3;
	}

	public void setPathogenTestResult3(PathogenTestResultType pathogenTestResult3) {
		this.pathogenTestResult3 = pathogenTestResult3;
	}

	@Order(96)
	public Boolean getPathogenTestVerified3() {
		return pathogenTestVerified3;
	}

	public void setPathogenTestVerified3(Boolean pathogenTestVerified3) {
		this.pathogenTestVerified3 = pathogenTestVerified3;
	}

	@Order(97)
	public String getOtherPathogenTestsDetails() {
		return otherPathogenTestsDetails;
	}

	public void setOtherPathogenTestsDetails(String otherPathogenTestsDetails) {
		this.otherPathogenTestsDetails = otherPathogenTestsDetails;
	}

	@Order(101)
	public AdditionalTestDto getAdditionalTest() {
		return additionalTest;
	}

	public void setAdditionalTest(AdditionalTestDto additionalTest) {
		this.additionalTest = additionalTest;
	}

	@Order(102)
	public String getOtherAdditionalTestsDetails() {
		return otherAdditionalTestsDetails;
	}

	public void setOtherAdditionalTestsDetails(String otherAdditionalTestsDetails) {
		this.otherAdditionalTestsDetails = otherAdditionalTestsDetails;
	}

	public CaseJurisdictionDto getAssociatedCaseJurisdiction() {
		return associatedCaseJurisdiction;
	}

	public ContactJurisdictionDto getAssociatedContactJurisdiction() {
		return associatedContactJurisdiction;
	}

	public static class AssociatedCase extends CaseReferenceDto {

		private static final long serialVersionUID = 4890448385381706557L;

		private String region;
		private String district;
		@PersonalData
		private String community;
		@PersonalData
		private String facility;

		public AssociatedCase(
			String uuid,
			String firstName,
			String lastName,
			String region,
			String district,
			String community,
			String facilityUuid,
			String facility,
			String facilityDetails) {
			super(uuid, firstName, lastName);

			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = FacilityHelper.buildFacilityString(facilityUuid, facility, facilityDetails);

		}

		public String getRegion() {
			return region;
		}

		public String getDistrict() {
			return district;
		}

		public String getCommunity() {
			return community;
		}

		public String getFacility() {
			return facility;
		}
	}

	public static class SampleExportPersonAddress implements Serializable {

		private static final long serialVersionUID = 466724930802680895L;

		private String region;
		private String district;
		@PersonalData
		private String community;
		@PersonalData
		private String city;
		@PersonalData
		private String address;

		public SampleExportPersonAddress(String region, String district, String community, String city, String address) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.city = city;
			this.address = address;
		}
	}
}
