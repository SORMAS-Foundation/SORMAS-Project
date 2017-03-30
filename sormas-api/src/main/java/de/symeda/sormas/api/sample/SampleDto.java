package de.symeda.sormas.api.sample;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class SampleDto extends SampleReferenceDto {

	private static final long serialVersionUID = -6975445672442728938L;

	public static final String I18N_PREFIX = "Sample";
	
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String SAMPLE_CODE = "sampleCode";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String LAB = "lab";
	public static final String OTHER_LAB = "otherLab";
	public static final String SHIPMENT_STATUS = "shipmentStatus";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String SUGGESTED_TYPE_OF_TEST = "suggestedTypeOfTest";
	
	private CaseReferenceDto associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Date sampleDateTime;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private FacilityReferenceDto lab;
	private FacilityReferenceDto otherLab;
	private ShipmentStatus shipmentStatus;
	private Date shipmentDate;
	private String shipmentDetails;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private SampleSource sampleSource;
	private SampleTestType suggestedTypeOfTest;
	
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
	public String getLabSampleID() {
		return labSampleID;
	}
	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}
	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}
	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}
	public FacilityReferenceDto getLab() {
		return lab;
	}
	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}
	public FacilityReferenceDto getOtherLab() {
		return otherLab;
	}
	public void setOtherLab(FacilityReferenceDto otherLab) {
		this.otherLab = otherLab;
	}
	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}
	public void setShipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getShipmentDate() {
		return shipmentDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getShipmentDetails() {
		return shipmentDetails;
	}
	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getReceivedDate() {
		return receivedDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public SampleSource getSampleSource() {
		return sampleSource;
	}
	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}
	public SampleTestType getSuggestedTypeOfTest() {
		return suggestedTypeOfTest;
	}
	public void setSuggestedTypeOfTest(SampleTestType suggestedTypeOfTest) {
		this.suggestedTypeOfTest = suggestedTypeOfTest;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleDto other = (SampleDto) obj;
		if (associatedCase == null) {
			if (other.associatedCase != null)
				return false;
		} else if (!associatedCase.equals(other.associatedCase))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (lab == null) {
			if (other.lab != null)
				return false;
		} else if (!lab.equals(other.lab))
			return false;
		if (labSampleID == null) {
			if (other.labSampleID != null)
				return false;
		} else if (!labSampleID.equals(other.labSampleID))
			return false;
		if (noTestPossibleReason == null) {
			if (other.noTestPossibleReason != null)
				return false;
		} else if (!noTestPossibleReason.equals(other.noTestPossibleReason))
			return false;
		if (otherLab == null) {
			if (other.otherLab != null)
				return false;
		} else if (!otherLab.equals(other.otherLab))
			return false;
		if (receivedDate == null) {
			if (other.receivedDate != null)
				return false;
		} else if (!receivedDate.equals(other.receivedDate))
			return false;
		if (reportDateTime == null) {
			if (other.reportDateTime != null)
				return false;
		} else if (!reportDateTime.equals(other.reportDateTime))
			return false;
		if (reportingUser == null) {
			if (other.reportingUser != null)
				return false;
		} else if (!reportingUser.equals(other.reportingUser))
			return false;
		if (sampleCode == null) {
			if (other.sampleCode != null)
				return false;
		} else if (!sampleCode.equals(other.sampleCode))
			return false;
		if (sampleDateTime == null) {
			if (other.sampleDateTime != null)
				return false;
		} else if (!sampleDateTime.equals(other.sampleDateTime))
			return false;
		if (sampleMaterial != other.sampleMaterial)
			return false;
		if (sampleMaterialText == null) {
			if (other.sampleMaterialText != null)
				return false;
		} else if (!sampleMaterialText.equals(other.sampleMaterialText))
			return false;
		if (shipmentDate == null) {
			if (other.shipmentDate != null)
				return false;
		} else if (!shipmentDate.equals(other.shipmentDate))
			return false;
		if (shipmentDetails == null) {
			if (other.shipmentDetails != null)
				return false;
		} else if (!shipmentDetails.equals(other.shipmentDetails))
			return false;
		if (shipmentStatus != other.shipmentStatus)
			return false;
		if (specimenCondition != other.specimenCondition)
			return false;
		return true;
	}
	
}
