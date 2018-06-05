package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SampleIndexDto implements Serializable {

	private static final long serialVersionUID = -6298614717044087479L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String UUID = "uuid";
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String CASE_REGION_UUID = "caseRegionUuid";
	public static final String CASE_DISTRICT = "caseDistrict";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String LAB = "lab";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String REFERRED = "referred";
	public static final String SAMPLE_TEST_RESULT = "sampleTestResult";
	public static final String SAMPLE_TEST_LAB_USER_NAME = "sampleTestLabUserName";
	
	private String uuid;
	private CaseReferenceDto associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Disease disease;
	private String diseaseDetails;
	private String caseRegionUuid;
	private DistrictReferenceDto caseDistrict;
	private boolean shipped;
	private boolean received;
	private boolean referred;
	private Date shipmentDate;
	private Date receivedDate;
	private FacilityReferenceDto lab;
	private SampleMaterial sampleMaterial;
	private SpecimenCondition specimenCondition;
	private SampleTestResultType sampleTestResult;
	private String sampleTestLabUserName;
	
	public SampleIndexDto(String uuid, String sampleCode, String labSampleId, 
			boolean shipped, Date shipmentDate, boolean received, Date receivedDate, 
			SampleMaterial sampleMaterial, SpecimenCondition specimenCondition,
			String labUuid, String labName, String referredSampleUuid, 
			String associatedCaseUuid, String associatedCaseFirstName, String associatedCaseLastName,
			Disease disease, String diseaseDetails, String caseRegionUuid, 
			String caseDistrictUuid, String caseDistrictName, SampleTestResultType sampleTestResult,
			String sampleTestLabUserFirstName, String sampleTestLabUserLastName) {
		this.uuid = uuid;
		this.associatedCase = new CaseReferenceDto(associatedCaseUuid, associatedCaseFirstName, associatedCaseLastName);
		this.sampleCode = sampleCode;
		this.labSampleID = labSampleId;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.caseRegionUuid = caseRegionUuid;
		this.caseDistrict = new DistrictReferenceDto(caseDistrictUuid, caseDistrictName);
		this.shipped = shipped;
		this.received = received;
		this.referred = referredSampleUuid != null;
		this.shipmentDate = shipmentDate;
		this.receivedDate = receivedDate;
		this.lab = new FacilityReferenceDto(labUuid, FacilityHelper.buildToString(labUuid, labName));
		this.sampleMaterial = sampleMaterial;
		this.specimenCondition = specimenCondition;
		this.sampleTestResult = sampleTestResult;
		this.sampleTestLabUserName = UserReferenceDto.buildCaption(sampleTestLabUserFirstName, sampleTestLabUserLastName, null);
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public boolean isReferred() {
		return referred;
	}
	public void setReferred(boolean referred) {
		this.referred = referred;
	}
	public String getCaseRegionUuid() {
		return caseRegionUuid;
	}
	public void setCaseRegionUuid(String caseRegionUuid) {
		this.caseRegionUuid = caseRegionUuid;
	}
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	
	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(uuid, getSampleMaterial(), getAssociatedCase().getUuid());
	}

	public SampleTestResultType getSampleTestResult() {
		return sampleTestResult;
	}

	public void setSampleTestResult(SampleTestResultType sampleTestResult) {
		this.sampleTestResult = sampleTestResult;
	}

	public String getSampleTestLabUserName() {
		return sampleTestLabUserName;
	}

	public void setSampleTestLabUserName(String sampleTestLabUserName) {
		this.sampleTestLabUserName = sampleTestLabUserName;
	}
}
