package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class SampleListEntryDto extends PseudonymizableIndexDto implements Serializable {

	public static final String I18N_PREFIX = "Sample";

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLING_REASON = "samplingReason";
	public static final String PATHOGEN_TEST_COUNT = "pathogenTestCount";

	private String uuid;
	private SampleMaterial sampleMaterial;
	private PathogenTestResultType pathogenTestResult;
	private SpecimenCondition specimenCondition;
	private boolean referred;
	private SamplePurpose samplePurpose;
	private boolean received;
	private Date receivedDate;
	private boolean shipped;
	private Date shipmentDate;
	private Date sampleDateTime;
	private FacilityReferenceDto lab;
	private SamplingReason samplingReason;
	private String samplingReasonDetails;
	private AdditionalTestingStatus additionalTestingStatus;
	private long pathogenTestCount;

	public SampleListEntryDto(
		String uuid,
		SampleMaterial sampleMaterial,
		PathogenTestResultType pathogenTestResult,
		SpecimenCondition specimenCondition,
		SamplePurpose samplePurpose,
		boolean referred,
		boolean received,
		Date receivedDate,
		boolean shipped,
		Date shipmentDate,
		Date sampleDateTime,
		FacilityReferenceDto lab,
		SamplingReason samplingReason,
		String samplingReasonDetails,
		AdditionalTestingStatus additionalTestingStatus,
		long pathogenTestCount) {

		this.uuid = uuid;
		this.sampleMaterial = sampleMaterial;
		this.pathogenTestResult = pathogenTestResult;
		this.specimenCondition = specimenCondition;
		this.samplePurpose = samplePurpose;
		this.referred = referred;
		this.received = received;
		this.receivedDate = receivedDate;
		this.shipped = shipped;
		this.shipmentDate = shipmentDate;
		this.sampleDateTime = sampleDateTime;
		this.lab = lab;
		this.samplingReason = samplingReason;
		this.samplingReasonDetails = samplingReasonDetails;
		this.additionalTestingStatus = additionalTestingStatus;
		this.pathogenTestCount = pathogenTestCount;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public boolean isReferred() {
		return referred;
	}

	public void setReferred(boolean referred) {
		this.referred = referred;
	}

	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	public Date getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public FacilityReferenceDto getLab() {
		return lab;
	}

	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}

	public SamplingReason getSamplingReason() {
		return samplingReason;
	}

	public void setSamplingReason(SamplingReason samplingReason) {
		this.samplingReason = samplingReason;
	}

	public String getSamplingReasonDetails() {
		return samplingReasonDetails;
	}

	public void setSamplingReasonDetails(String samplingReasonDetails) {
		this.samplingReasonDetails = samplingReasonDetails;
	}

	public AdditionalTestingStatus getAdditionalTestingStatus() {
		return additionalTestingStatus;
	}

	public void setAdditionalTestingStatus(AdditionalTestingStatus additionalTestingStatus) {
		this.additionalTestingStatus = additionalTestingStatus;
	}

	public long getPathogenTestCount() {
		return pathogenTestCount;
	}

	public void setPathogenTestCount(long pathogenTestCount) {
		this.pathogenTestCount = pathogenTestCount;
	}

	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(uuid, getSampleMaterial(), null, null, null);
	}
}
