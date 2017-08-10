package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SampleIndexDto extends SampleReferenceDto {

	private static final long serialVersionUID = 3830865465145040944L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String DISEASE = "disease";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String LGA = "lga";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String LAB = "lab";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String LAB_USER = "labUser";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_RESULT = "testResult";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String REFERRED_TO = "referredTo";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	
	private CaseReferenceDto associatedCase;
	private Disease disease;
	private String sampleCode;
	private String labSampleID;
	private DistrictReferenceDto lga;
	private Date shipmentDate;
	private Date receivedDate;
	private FacilityReferenceDto lab;
	private SampleMaterial sampleMaterial;
	private UserReferenceDto labUser;
	private SampleTestType testType;
	private SampleTestResultType testResult;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private SampleReferenceDto referredTo;
	private boolean shipped;
	private boolean received;
	
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
	public DistrictReferenceDto getLga() {
		return lga;
	}
	public void setLga(DistrictReferenceDto lga) {
		this.lga = lga;
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
	public SampleTestType getTestType() {
		return testType;
	}
	public void setTestType(SampleTestType testType) {
		this.testType = testType;
	}
	public SampleTestResultType getTestResult() {
		return testResult;
	}
	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}
	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}	
	public SampleReferenceDto getReferredTo() {
		return referredTo;
	}
	public void setReferredTo(SampleReferenceDto referredTo) {
		this.referredTo = referredTo;
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
	
}
