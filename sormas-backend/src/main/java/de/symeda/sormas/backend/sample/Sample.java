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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity(name="samples")
@Audited
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
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String SUGGESTED_TYPE_OF_TEST = "suggestedTypeOfTest";
	public static final String REFERRED_TO = "referredTo";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	
	private Case associatedCase;
	private String sampleCode;
	private String labSampleID;
	private Date sampleDateTime;

	private Date reportDateTime;
	private User reportingUser;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;

	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private Facility lab;
	private Date shipmentDate;
	private String shipmentDetails;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private SampleSource sampleSource;
	private SampleTestType suggestedTypeOfTest;
	private Sample referredTo;
	private boolean shipped;
	private boolean received;
	
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

	@OneToOne(cascade = {})
	@JoinColumn(nullable = true)
	public Sample getReferredTo() {
		return referredTo;
	}
	public void setReferredTo(Sample referredTo) {
		this.referredTo = referredTo;
	}
	
	@Column
	public boolean isShipped() {
		return shipped;
	}
	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}
	
	@Column
	public boolean isReceived() {
		return received;
	}
	public void setReceived(boolean received) {
		this.received = received;
	}
	
	@Override
	public String toString() {
		String materialString = sampleMaterial == null ? "" : sampleMaterial.toString();
		String sampleString = materialString.isEmpty() ? "Sample" : "sample";
		return materialString + " " + sampleString + " for case " + DataHelper.getShortUuid(associatedCase.getUuid());
	}
	
	public Double getReportLat() {
		return reportLat;
	}
	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}
	public Double getReportLon() {
		return reportLon;
	}
	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}
	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}
	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}
	
	
}
