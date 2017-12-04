package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SampleIndexDto extends EntityDto {

	private static final long serialVersionUID = 3830865465145040944L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String CASE_REGION = "caseRegion";
	public static final String CASE_DISTRICT = "caseDistrict";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String LAB = "lab";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String REFERRED = "referred";
	
	private String associatedCaseUuid;
	private CaseReferenceDto associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Disease disease;
	private String diseaseDetails;
	private String caseRegionUuid;
	private RegionReferenceDto caseRegion;
	private String caseDistrictUuid;
	private DistrictReferenceDto caseDistrict;
	private boolean shipped;
	private boolean received;
	private String referredSampleUuid;
	private boolean referred;
	private Date shipmentDate;
	private Date receivedDate;
	private String labUuid;
	private FacilityReferenceDto lab;
	private SampleMaterial sampleMaterial;
	private UserReferenceDto labUser;
	private SpecimenCondition specimenCondition;
	private SampleTestResultType testResult;
	
	public SampleIndexDto(String uuid, String associatedCaseUuid, String sampleCode, String labSampleId, Disease disease, String diseaseDetails,
			String caseRegionUuid, String caseDistrictUuid, boolean shipped, boolean received, String referredSampleUuid, Date shipmenteDate,
			Date receivedDate, String labUuid, SampleMaterial sampleMaterial, SpecimenCondition specimenCondition) {
		super.setUuid(uuid);
		this.associatedCaseUuid = associatedCaseUuid;
		this.sampleCode = sampleCode;
		this.labSampleID = labSampleId;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.caseRegionUuid = caseRegionUuid;
		this.caseDistrictUuid = caseDistrictUuid;
		this.shipped = shipped;
		this.received = received;
		this.referredSampleUuid = referredSampleUuid;
		this.shipmentDate = shipmenteDate;
		this.receivedDate = receivedDate;
		this.labUuid = labUuid;
		this.sampleMaterial = sampleMaterial;
		this.specimenCondition = specimenCondition;
	}
	
	public CaseReferenceDto getAssociatedCase() {
		return associatedCase;
	}
	public void setAssociatedCase(CaseReferenceDto associatedCase) {
		this.associatedCase = associatedCase;
	}
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	public String getDiseaseDetails() {
		return diseaseDetails;
	}
	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public String getLabSampleID() {
		return labSampleID;
	}
	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}
	public DistrictReferenceDto getCaseDistrict() {
		return caseDistrict;
	}
	public void setCaseDistrict(DistrictReferenceDto caseDistrict) {
		this.caseDistrict = caseDistrict;
	}
	public Date getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public FacilityReferenceDto getLab() {
		return lab;
	}
	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}
	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}
	public UserReferenceDto getLabUser() {
		return labUser;
	}
	public void setLabUser(UserReferenceDto labUser) {
		this.labUser = labUser;
	}
	public SampleTestResultType getTestResult() {
		return testResult;
	}
	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}
	public boolean isShipped() {
		return shipped;
	}
	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}
	public boolean isReceived() {
		return received;
	}
	public void setReceived(boolean received) {
		this.received = received;
	}
	public RegionReferenceDto getCaseRegion() {
		return caseRegion;
	}
	public void setCaseRegion(RegionReferenceDto caseRegion) {
		this.caseRegion = caseRegion;
	}
	public String getAssociatedCaseUuid() {
		return associatedCaseUuid;
	}
	public void setAssociatedCaseUuid(String associatedCaseUuid) {
		this.associatedCaseUuid = associatedCaseUuid;
	}
	public boolean isReferred() {
		return referred;
	}
	public void setReferred(boolean referred) {
		this.referred = referred;
	}
	public String getLabUuid() {
		return labUuid;
	}
	public void setLabUuid(String labUuid) {
		this.labUuid = labUuid;
	}
	public String getCaseRegionUuid() {
		return caseRegionUuid;
	}
	public void setCaseRegionUuid(String caseRegionUuid) {
		this.caseRegionUuid = caseRegionUuid;
	}
	public String getCaseDistrictUuid() {
		return caseDistrictUuid;
	}
	public void setCaseDistrictUuid(String caseDistrictUuid) {
		this.caseDistrictUuid = caseDistrictUuid;
	}
	public String getReferredSampleUuid() {
		return referredSampleUuid;
	}
	public void setReferredSampleUuid(String referredSampleUuid) {
		this.referredSampleUuid = referredSampleUuid;
	}
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	
	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(getUuid());
	}

}
