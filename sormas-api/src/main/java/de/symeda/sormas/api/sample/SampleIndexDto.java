package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SampleIndexDto extends SampleReferenceDto {

	private static final long serialVersionUID = 3830865465145040944L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String SHIPMENT_STATUS = "shipmentStatus";
	public static final String LGA = "lga";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String LAB = "lab";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String LAB_USER = "labUser";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_RESULT = "testResult";
	public static final String NO_TEST_POSSIBLE = "noTestPossible";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	
	private CaseReferenceDto associatedCase;
	private String sampleCode;
	private ShipmentStatus shipmentStatus;
	private DistrictReferenceDto lga;
	private Date shipmentDate;
	private FacilityReferenceDto lab;
	private SampleMaterial sampleMaterial;
	private UserReferenceDto labUser;
	private SampleTestType testType;
	private SampleTestResultType testResult;
	private boolean noTestPossible;
	private String noTestPossibleReason;
	
	public CaseReferenceDto getAssociatedCase() {
		return associatedCase;
	}
	public void setAssociatedCase(CaseReferenceDto associatedCase) {
		this.associatedCase = associatedCase;
	}
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}
	public void setShipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
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
	public boolean isNoTestPossible() {
		return noTestPossible;
	}
	public void setNoTestPossible(boolean noTestPossible) {
		this.noTestPossible = noTestPossible;
	}
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}
	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}
	
}
