/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;

public class SampleIndexDto implements Serializable {

	private static final long serialVersionUID = -6298614717044087479L;

	public static final String I18N_PREFIX = "Sample";

	public static final String UUID = "uuid";
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String ASSOCIATED_CONTACT = "associatedContact";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT = "district";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SHIPMENT_DATE = "shipmentDate";
	public static final String RECEIVED_DATE = "receivedDate";
	public static final String LAB = "lab";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_PURPOSE = "samplePurpose";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String REFERRED = "referred";
	public static final String PATHOGEN_TEST_RESULT = "pathogenTestResult";
	public static final String PATHOGEN_TEST_LAB_USER_NAME = "pathogenTestLabUserName";
	public static final String ADDITIONAL_TESTING_STATUS = "additionalTestingStatus";

	private String uuid;
	private CaseReferenceDto associatedCase;
	private ContactReferenceDto associatedContact;
	private String epidNumber;
	private String labSampleID;
	private Disease disease;
	private String diseaseDetails;
	private String regionUuid;
	private DistrictReferenceDto district;
	private boolean shipped;
	private boolean received;
	private boolean referred;
	private Date sampleDateTime;
	private Date shipmentDate;
	private Date receivedDate;
	private FacilityReferenceDto lab;
	private SampleMaterial sampleMaterial;
	private SamplePurpose samplePurpose;
	private SpecimenCondition specimenCondition;
	private PathogenTestResultType pathogenTestResult;
	private AdditionalTestingStatus additionalTestingStatus;

	public SampleIndexDto(String uuid, String epidNumber, String labSampleId, Date sampleDateTime,
			boolean shipped, Date shipmentDate, boolean received, Date receivedDate, 
			SampleMaterial sampleMaterial, SamplePurpose samplePurpose, SpecimenCondition specimenCondition,
			String labUuid, String labName, String referredSampleUuid, 
			String associatedCaseUuid, String associatedCaseFirstName, String associatedCaseLastName,
			String associatedContactUuid, String associatedContactFirstName, String associatedContactLastName,
			Disease disease, String diseaseDetails, String regionUuid,
			String districtUuid, String districtName, PathogenTestResultType pathogenTestResult,
			Boolean additionalTestingRequested, Boolean additionalTestPerformed) {
		this.uuid = uuid;
		if(associatedCaseUuid != null) {
			this.associatedCase = new CaseReferenceDto(associatedCaseUuid, associatedCaseFirstName, associatedCaseLastName);
		}
		if(associatedContactUuid != null) {
			this.associatedContact = new ContactReferenceDto(associatedContactUuid, associatedContactFirstName, associatedContactLastName, null, null);
		}
		this.epidNumber = epidNumber;
		this.labSampleID = labSampleId;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.regionUuid = regionUuid;
		if (districtUuid != null) {
			this.district = new DistrictReferenceDto(districtUuid, districtName);
		}
		this.shipped = shipped;
		this.received = received;
		this.referred = referredSampleUuid != null;
		this.sampleDateTime = sampleDateTime;
		this.shipmentDate = shipmentDate;
		this.receivedDate = receivedDate;
		this.lab = new FacilityReferenceDto(labUuid, FacilityHelper.buildFacilityString(labUuid, labName));
		this.sampleMaterial = sampleMaterial;
		this.samplePurpose = samplePurpose;
		this.specimenCondition = specimenCondition;
		this.pathogenTestResult = pathogenTestResult;
		this.additionalTestingStatus =  Boolean.TRUE.equals(additionalTestPerformed) ? AdditionalTestingStatus.PERFORMED : 
			(Boolean.TRUE.equals(additionalTestingRequested) ? AdditionalTestingStatus.REQUESTED : AdditionalTestingStatus.NOT_REQUESTED);
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
	public ContactReferenceDto getAssociatedContact() {
		return associatedContact;
	}
	public void setAssociatedContact(ContactReferenceDto associatedContact) {
		this.associatedContact = associatedContact;
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
	public String getEpidNumber() {
		return epidNumber;
	}
	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}
	public String getLabSampleID() {
		return labSampleID;
	}
	public void setLabSampleID(String labSampleID) {
		this.labSampleID = labSampleID;
	}
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
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
	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}
	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
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
	public String getRegionUuid() {
		return regionUuid;
	}
	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}
	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}
	public SampleReferenceDto toReference() {
		return new SampleReferenceDto(uuid, getSampleMaterial(),
				getAssociatedCase() != null ? getAssociatedCase().getUuid() : null,
				getAssociatedContact() != null ? getAssociatedContact().getUuid() : null);
	}
	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}
	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}
	public Date getSampleDateTime() {
		return sampleDateTime;
	}
	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
	public AdditionalTestingStatus getAdditionalTestingStatus() {
		return additionalTestingStatus;
	}
	public void setAdditionalTestingStatus(AdditionalTestingStatus additionalTestingStatus) {
		this.additionalTestingStatus = additionalTestingStatus;
	}

}
