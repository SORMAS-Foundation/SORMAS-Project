package de.symeda.sormas.backend.sample;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity(name="samples")
public class Sample extends AbstractDomainObject {

	private static final long serialVersionUID = -7196712070188634978L;
	
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
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String SUGGESTED_TYPE_OF_TEST = "suggestedTypeOfTest";
	
	private Case associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Date sampleDateTime;
	private Date reportDateTime;
	private User reportingUser;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private Facility lab;
	private Facility otherLab;
	private ShipmentStatus shipmentStatus;
	private Date shipmentDate;
	private String shipmentDetails;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private SampleSource sampleSource;
	private SampleTestType suggestedTypeOfTest;
	
	private List<SampleTest> sampleTests;
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Case getAssociatedCase() {
		return associatedCase;
	}
	public void setAssociatedCase(Case associatedCase) {
		this.associatedCase = associatedCase;
	}
	
	@Column(length=512)
	public String getSampleCode() {
		return sampleCode;
	}
	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}
	
	@Column(length=512)
	public String getLabSampleID() {
		return labSampleID;
	}
	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}
	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}
	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}
	
	@Column(length=512)
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}
	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Facility getLab() {
		return lab;
	}
	public void setLab(Facility lab) {
		this.lab = lab;
	}
	
	@ManyToOne(cascade = {})
	public Facility getOtherLab() {
		return otherLab;
	}
	public void setOtherLab(Facility otherLab) {
		this.otherLab = otherLab;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public ShipmentStatus getShipmentStatus() {
		return shipmentStatus;
	}
	public void setShipmentStatus(ShipmentStatus shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	
	@Column(length=512)
	public String getShipmentDetails() {
		return shipmentDetails;
	}
	public void setShipmentDetails(String shipmentDetails) {
		this.shipmentDetails = shipmentDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	
	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	
	@Column(length=512)
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}
	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}
	
	@OneToMany(cascade = {}, mappedBy = SampleTest.SAMPLE)
	public List<SampleTest> getSampleTests() {
		return sampleTests;
	}
	public void setSampleTests(List<SampleTest> sampleTests) {
		this.sampleTests = sampleTests;
	}
	
	@Column(length=512)
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Enumerated(EnumType.STRING)
	public SampleSource getSampleSource() {
		return sampleSource;
	}
	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}
	
	@Enumerated(EnumType.STRING)
	public SampleTestType getSuggestedTypeOfTest() {
		return suggestedTypeOfTest;
	}
	public void setSuggestedTypeOfTest(SampleTestType suggestedTypeOfTest) {
		this.suggestedTypeOfTest = suggestedTypeOfTest;
	}
	
	@Override
	public String toString() {
		String materialString = sampleMaterial == null ? "" : sampleMaterial.toString();
		String sampleString = materialString.isEmpty() ? "Sample" : "sample";
		return materialString + " " + sampleString + " for case " + DataHelper.getShortUuid(associatedCase.getUuid());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sample other = (Sample) obj;
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
		if (sampleTests == null) {
			if (other.sampleTests != null)
				return false;
		} else if (!sampleTests.equals(other.sampleTests))
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
