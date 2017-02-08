package de.symeda.sormas.backend.sample;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.ShipmentStatus;
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
	public static final String NO_TEST_POSSIBLE = "noTestPossible";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	
	private Case associatedCase;
	private String sampleCode;
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
	private boolean noTestPossible;
	private String noTestPossibleReason;
	
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
	
	public boolean isNoTestPossible() {
		return noTestPossible;
	}
	public void setNoTestPossible(boolean noTestPossible) {
		this.noTestPossible = noTestPossible;
	}
	
	@Column(length=512)
	public String getNoTestPossibleReason() {
		return noTestPossibleReason;
	}
	public void setNoTestPossibleReason(String noTestPossibleReason) {
		this.noTestPossibleReason = noTestPossibleReason;
	}
	
	@Override
	public String toString() {
		String materialString = sampleMaterial == null ? "" : sampleMaterial.toString();
		String sampleString = materialString.isEmpty() ? "Sample" : "sample";
		return materialString + " " + sampleString + " for case " + DataHelper.getShortUuid(associatedCase.getUuid());
	}

}
