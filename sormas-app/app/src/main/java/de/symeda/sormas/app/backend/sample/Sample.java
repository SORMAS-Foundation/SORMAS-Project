package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

@Entity(name=Sample.TABLE_NAME)
@DatabaseTable(tableName = Sample.TABLE_NAME)
public class Sample extends AbstractDomainObject {

    private static final long serialVersionUID = -7196712070188634978L;

    public static final String TABLE_NAME = "samples";
    public static final String I18N_PREFIX = "Sample";

    public static final String SAMPLE_DATE_TIME = "sampleDateTime";
    public static final String ASSOCIATED_CASE = "associatedCase";
    public static final String REFERRED_TO = "referredTo";
    public static final String SHIPPED = "shipped";
    public static final String RECEIVED = "received";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Case associatedCase;

    @Column(length = 512)
    private String sampleCode;

    @Column(length = 512)
    private String labSampleID;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date sampleDateTime;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date reportDateTime;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User reportingUser;

    @DatabaseField
    private Double reportLat;
    @DatabaseField
    private Double reportLon;
    @DatabaseField
    private Float reportLatLonAccuracy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleMaterial sampleMaterial;

    @Column(length = 512)
    private String sampleMaterialText;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3, canBeNull = false)
    private Facility lab;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date shipmentDate;

    @Column(length = 512)
    private String shipmentDetails;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date receivedDate;

    @Enumerated(EnumType.STRING)
    private SpecimenCondition specimenCondition;

    @Column(length = 512)
    private String noTestPossibleReason;

    @Column(length = 512)
    private String comment;

    @Enumerated(EnumType.STRING)
    private SampleSource sampleSource;

    @Enumerated(EnumType.STRING)
    private SampleTestType suggestedTypeOfTest;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Sample referredTo;

    @DatabaseField
    private boolean shipped;

    @DatabaseField
    private boolean received;

    @DatabaseField(defaultValue = "", canBeNull = false)
    private String shipmentStatus;

    @DatabaseField(foreign = true)
    private Facility otherLab;

    public Case getAssociatedCase() {
        return associatedCase;
    }

    public void setAssociatedCase(Case associatedCase) {
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

    public Date getSampleDateTime() {
        return sampleDateTime;
    }

    public void setSampleDateTime(Date sampleDateTime) {
        this.sampleDateTime = sampleDateTime;
    }

    public Date getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(Date reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public User getReportingUser() {
        return reportingUser;
    }

    public void setReportingUser(User reportingUser) {
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

    public Facility getLab() {
        return lab;
    }

    public void setLab(Facility lab) {
        this.lab = lab;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getShipmentDetails() {
        return shipmentDetails;
    }

    public void setShipmentDetails(String shipmentDetails) {
        this.shipmentDetails = shipmentDetails;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getNoTestPossibleReason() {
        return noTestPossibleReason;
    }

    public void setNoTestPossibleReason(String noTestPossibleReason) {
        this.noTestPossibleReason = noTestPossibleReason;
    }

    public SpecimenCondition getSpecimenCondition() {
        return specimenCondition;
    }

    public void setSpecimenCondition(SpecimenCondition specimenCondition) {
        this.specimenCondition = specimenCondition;
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

    public Sample getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(Sample referredTo) {
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

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }

    @Override
    public String toString() {
        return super.toString() + DateHelper.formatShortDate(getSampleDateTime());
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
