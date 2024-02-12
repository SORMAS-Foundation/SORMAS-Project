/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.SAMPLES_LAB)
public class SampleDto extends SormasToSormasShareableDto implements ISample {

	private static final long serialVersionUID = -6975445672442728938L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 6210;

	public static final String I18N_PREFIX = "Sample";

	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String FIELD_SAMPLE_ID = "fieldSampleID";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String SAMPLE_PURPOSE = "samplePurpose";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String SHIPMENT_DETAILS = "shipmentDetails";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String NO_TEST_POSSIBLE_REASON = "noTestPossibleReason";
	public static final String COMMENT = "comment";
	public static final String SAMPLE_SOURCE = "sampleSource";
	public static final String REFERRED_TO = "referredTo";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String PATHOGEN_TESTING_REQUESTED = "pathogenTestingRequested";
	public static final String ADDITIONAL_TESTING_REQUESTED = "additionalTestingRequested";
	public static final String REQUESTED_PATHOGEN_TESTS = "requestedPathogenTests";
	public static final String REQUESTED_ADDITIONAL_TESTS = "requestedAdditionalTests";
	public static final String PATHOGEN_TEST_RESULT = "pathogenTestResult";
	public static final String REQUESTED_OTHER_PATHOGEN_TESTS = "requestedOtherPathogenTests";
	public static final String REQUESTED_OTHER_ADDITIONAL_TESTS = "requestedOtherAdditionalTests";
	public static final String SAMPLING_REASON = "samplingReason";
	public static final String SAMPLING_REASON_DETAILS = "samplingReasonDetails";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	private CaseReferenceDto associatedCase;
	private ContactReferenceDto associatedContact;
	private EventParticipantReferenceDto associatedEventParticipant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labSampleID;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String fieldSampleID;
	@NotNull(message = Validations.requiredField)
	private Date sampleDateTime;

	@NotNull(message = Validations.validReportDateTime)
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	@SensitiveData
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@SensitiveData
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;

	private Float reportLatLonAccuracy;

	@NotNull(message = Validations.requiredField)
	private SampleMaterial sampleMaterial;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String sampleMaterialText;
	@NotNull(message = Validations.requiredField)
	private SamplePurpose samplePurpose;

	private FacilityReferenceDto lab;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labDetails;
	private Date shipmentDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String shipmentDetails;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String noTestPossibleReason;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String comment;
	private SampleSource sampleSource;
	private SampleReferenceDto referredTo;
	private boolean shipped;
	private boolean received;
	private PathogenTestResultType pathogenTestResult;

	private Boolean pathogenTestingRequested;
	private Boolean additionalTestingRequested;
	private Set<PathogenTestType> requestedPathogenTests;
	private Set<AdditionalTestType> requestedAdditionalTests;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String requestedOtherPathogenTests;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String requestedOtherAdditionalTests;

	private SamplingReason samplingReason;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String samplingReasonDetails;

	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	@ImportIgnore
	public CaseReferenceDto getAssociatedCase() {
		return associatedCase;
	}

	public void setAssociatedCase(CaseReferenceDto associatedCase) {
		this.associatedCase = associatedCase;
	}

	@ImportIgnore
	public ContactReferenceDto getAssociatedContact() {
		return associatedContact;
	}

	public void setAssociatedContact(ContactReferenceDto associatedContact) {
		this.associatedContact = associatedContact;
	}

	@ImportIgnore
	public EventParticipantReferenceDto getAssociatedEventParticipant() {
		return associatedEventParticipant;
	}

	public void setAssociatedEventParticipant(EventParticipantReferenceDto associatedEventParticipant) {
		this.associatedEventParticipant = associatedEventParticipant;
	}

	public String getLabSampleID() {
		return labSampleID;
	}

	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}

	public String getFieldSampleID() {
		return fieldSampleID;
	}

	public void setFieldSampleID(String fieldSampleID) {
		this.fieldSampleID = fieldSampleID;
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

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
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

	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
	}

	public FacilityReferenceDto getLab() {
		return lab;
	}

	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
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

	@ImportIgnore
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

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	@ImportIgnore
	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	@ImportIgnore
	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	@ImportIgnore
	public Set<PathogenTestType> getRequestedPathogenTests() {
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;
	}

	@ImportIgnore
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;
	}

	@ImportIgnore
	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	@ImportIgnore
	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
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

	public static SampleDto build(UserReferenceDto userRef, CaseReferenceDto caseRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedCase(caseRef);
		return sampleDto;
	}

	public static SampleDto build(UserReferenceDto userRef, EventParticipantReferenceDto eventParticipantRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedEventParticipant(eventParticipantRef);
		return sampleDto;
	}

	public static SampleDto build(UserReferenceDto userRef, ContactReferenceDto contactRef) {

		final SampleDto sampleDto = getSampleDto(userRef);
		sampleDto.setAssociatedContact(contactRef);
		return sampleDto;
	}

	private static SampleDto getSampleDto(UserReferenceDto userRef) {

		SampleDto sample = new SampleDto();
		sample.setUuid(DataHelper.createUuid());

		sample.setReportingUser(userRef);
		sample.setReportDateTime(new Date());
		sample.setPathogenTestResult(PathogenTestResultType.PENDING);

		return sample;
	}

	public static SampleDto buildReferralDto(UserReferenceDto userRef, SampleDto referredSample) {

		final SampleDto sample;
		final CaseReferenceDto associatedCase = referredSample.getAssociatedCase();
		final ContactReferenceDto associatedContact = referredSample.getAssociatedContact();
		final EventParticipantReferenceDto associatedEventParticipant = referredSample.getAssociatedEventParticipant();
		if (associatedCase != null) {
			sample = build(userRef, associatedCase);
		} else if (associatedContact != null) {
			sample = build(userRef, associatedContact);
		} else {
			sample = build(userRef, associatedEventParticipant);
		}
		migrateAttributesOfPhysicalSample(referredSample, sample);

		return sample;
	}

	/**
	 * The physical sample is neither the source, nor the target. This method is about migrating the attributes that belong to the real
	 * (physical) sample out there in the labs.
	 * Source and target should both refer to the physical sample, but have different values for some attributes. For example, the
	 * specimenCondition may be different in source and target.
	 * In one lab (source), the specimenCondition may be ADEQUATE. But then during transport to another lab (target) the specimenCondition
	 * can change to NOT_ADEQUATE.
	 *
	 * In contrast, the attributes of the physical sample don't change (e.g. samplingReason) and thus should be migrated when a sample
	 * referral is created in SORMAS.
	 */
	private static void migrateAttributesOfPhysicalSample(SampleDto source, SampleDto target) {
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSampleSource(source.getSampleSource());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSamplingReason(source.getSamplingReason());
		target.setSamplingReasonDetails(source.getSamplingReasonDetails());
		target.setSamplePurpose(source.getSamplePurpose());
	}

	@ImportIgnore
	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	@ImportIgnore
	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	@ImportIgnore
	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(
			getUuid(),
			sampleMaterial,
			associatedCase != null ? associatedCase.getUuid() : null,
			associatedContact != null ? associatedContact.getUuid() : null,
			associatedEventParticipant != null ? associatedEventParticipant.getUuid() : null);
	}

	@Override
	public SampleDto clone() throws CloneNotSupportedException {
		return (SampleDto) super.clone();
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
