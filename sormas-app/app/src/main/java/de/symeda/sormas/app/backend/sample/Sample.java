/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.sample;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DateFormatHelper;

@Entity(name = Sample.TABLE_NAME)
@DatabaseTable(tableName = Sample.TABLE_NAME)
public class Sample extends AbstractDomainObject {

	private static final long serialVersionUID = -7196712070188634978L;

	public static final String TABLE_NAME = "samples";
	public static final String I18N_PREFIX = "Sample";

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String ASSOCIATED_CASE = "associatedCase";
	public static final String REFERRED_TO_UUID = "referredToUuid";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	public static final String LAB_SAMPLE_ID = "labSampleID";
	public static final String FIELD_SAMPLE_ID = "fieldSampleID";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Case associatedCase;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Contact associatedContact;

	@Column(length = 512)
	private String labSampleID;

	@Column(length = 512)
	private String fieldSampleID;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date sampleDateTime;

	@DatabaseField(dataType = DataType.DATE_LONG)
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
	private SampleMaterial sampleMaterial;

	@Column(length = 512)
	private String sampleMaterialText;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility lab;

	@Column(length = 512)
	private String labDetails;

	@Enumerated(EnumType.STRING)
	private SamplePurpose samplePurpose;

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

	@DatabaseField
	private String referredToUuid;

	@DatabaseField
	private boolean shipped;

	@DatabaseField
	private boolean received;

	@Enumerated(EnumType.STRING)
	private PathogenTestResultType pathogenTestResult;

	@DatabaseField
	private Boolean pathogenTestingRequested;

	@DatabaseField
	private Boolean additionalTestingRequested;

	@Column(length = 512)
	private String requestedPathogenTestsString;

	@Column(length = 512)
	private String requestedAdditionalTestsString;

	@Transient
	private Set<PathogenTestType> requestedPathogenTests;

	@Transient
	private Set<AdditionalTestType> requestedAdditionalTests;

	@Column(length = 512)
	private String requestedOtherPathogenTests;

	@Column(length = 512)
	private String requestedOtherAdditionalTests;

	public Case getAssociatedCase() {
		return associatedCase;
	}

	public void setAssociatedCase(Case associatedCase) {
		this.associatedCase = associatedCase;
	}

	public Contact getAssociatedContact() {
		return associatedContact;
	}

	public void setAssociatedContact(Contact associatedContact) {
		this.associatedContact = associatedContact;
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

	public String getFieldSampleID() {
		return fieldSampleID;
	}

	public void setFieldSampleID(String fieldSampleID) {
		this.fieldSampleID = fieldSampleID;
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

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	public SamplePurpose getSamplePurpose() {
		return samplePurpose;
	}

	public void setSamplePurpose(SamplePurpose samplePurpose) {
		this.samplePurpose = samplePurpose;
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

	public String getReferredToUuid() {
		return referredToUuid;
	}

	public void setReferredToUuid(String referredToUuid) {
		this.referredToUuid = referredToUuid;
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

	public Boolean getPathogenTestingRequested() {
		return pathogenTestingRequested;
	}

	public void setPathogenTestingRequested(Boolean pathogenTestingRequested) {
		this.pathogenTestingRequested = pathogenTestingRequested;
	}

	public Boolean getAdditionalTestingRequested() {
		return additionalTestingRequested;
	}

	public void setAdditionalTestingRequested(Boolean additionalTestingRequested) {
		this.additionalTestingRequested = additionalTestingRequested;
	}

	public String getRequestedPathogenTestsString() {
		return requestedPathogenTestsString;
	}

	public void setRequestedPathogenTestsString(String requestedPathogenTestsString) {
		this.requestedPathogenTestsString = requestedPathogenTestsString;
		requestedPathogenTests = null;
	}

	public String getRequestedAdditionalTestsString() {
		return requestedAdditionalTestsString;
	}

	public void setRequestedAdditionalTestsString(String requestedAdditionalTestsString) {
		this.requestedAdditionalTestsString = requestedAdditionalTestsString;
		requestedAdditionalTests = null;
	}

	@Transient
	public Set<PathogenTestType> getRequestedPathogenTests() {
		if (requestedPathogenTests == null) {
			requestedPathogenTests = new HashSet<>();
			if (!StringUtils.isEmpty(requestedPathogenTestsString)) {
				String[] testTypes = requestedPathogenTestsString.split(",");
				for (String testType : testTypes) {
					requestedPathogenTests.add(PathogenTestType.valueOf(testType));
				}
			}
		}
		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<PathogenTestType> requestedPathogenTests) {
		this.requestedPathogenTests = requestedPathogenTests;

		if (this.requestedPathogenTests == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (PathogenTestType test : requestedPathogenTests) {
			sb.append(test.name());
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		requestedPathogenTestsString = sb.toString();
	}

	@Transient
	public Set<AdditionalTestType> getRequestedAdditionalTests() {
		if (requestedAdditionalTests == null) {
			requestedAdditionalTests = new HashSet<>();
			if (!StringUtils.isEmpty(requestedAdditionalTestsString)) {
				String[] testTypes = requestedAdditionalTestsString.split(",");
				for (String testType : testTypes) {
					requestedAdditionalTests.add(AdditionalTestType.valueOf(testType));
				}
			}
		}
		return requestedAdditionalTests;
	}

	public void setRequestedAdditionalTests(Set<AdditionalTestType> requestedAdditionalTests) {
		this.requestedAdditionalTests = requestedAdditionalTests;

		if (this.requestedAdditionalTests == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (AdditionalTestType test : requestedAdditionalTests) {
			sb.append(test.name());
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		requestedAdditionalTestsString = sb.toString();
	}

	public String getRequestedOtherPathogenTests() {
		return requestedOtherPathogenTests;
	}

	public void setRequestedOtherPathogenTests(String requestedOtherPathogenTests) {
		this.requestedOtherPathogenTests = requestedOtherPathogenTests;
	}

	public String getRequestedOtherAdditionalTests() {
		return requestedOtherAdditionalTests;
	}

	public void setRequestedOtherAdditionalTests(String requestedOtherAdditionalTests) {
		this.requestedOtherAdditionalTests = requestedOtherAdditionalTests;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + " " + DateFormatHelper.formatLocalDate(getSampleDateTime());
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
