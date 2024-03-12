package de.symeda.sormas.api.environment;

import java.util.Date;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class EnvironmentIndexDto extends AbstractUuidDto implements Pseudonymizable {

	public static final String I18N_PREFIX = "Environment";

	public static final String EXTERNAL_ID = "externalId";
	public static final String ENVIRONMENT_NAME = "environmentName";
	public static final String ENVIRONMENT_MEDIA = "environmentMedia";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	private String externalId;
	private String environmentName;
	private EnvironmentMedia environmentMedia;

	private String region;
	private String district;
	@PersonalData
	@SensitiveData
	private String community;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double latitude;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double longitude;
	@PersonalData()
	@SensitiveData()
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String city;
	private Date reportDate;
	private InvestigationStatus investigationStatus;
	private boolean inJurisdiction;
	private boolean pseudonymized;
	private DeletionReason deletionReason;
	private String otherDeletionReason;

	public EnvironmentIndexDto(
		String uuid,
		String externalId,
		String environmentName,
		EnvironmentMedia environmentMedia,
		String region,
		String district,
		String community,
		Double latitude,
		Double longitude,
		String postalCode,
		String city,
		Date reportDate,
		InvestigationStatus investigationStatus,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean inJurisdiction) {

		super(uuid);
		this.externalId = externalId;
		this.environmentName = environmentName;
		this.environmentMedia = environmentMedia;
		this.region = region;
		this.district = district;
		this.community = community;
		this.latitude = latitude;
		this.longitude = longitude;
		this.postalCode = postalCode;
		this.city = city;
		this.reportDate = reportDate;
		this.investigationStatus = investigationStatus;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		this.inJurisdiction = inJurisdiction;
	}

	public EnvironmentIndexDto(String uuid) {
		super(uuid);
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public EnvironmentMedia getEnvironmentMedia() {
		return environmentMedia;
	}

	public void setEnvironmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
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

	@Override
	public boolean isPseudonymized() {
		return pseudonymized;
	}

	@Override
	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
	}

	@Override
	public boolean isInJurisdiction() {
		return inJurisdiction;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {
		this.inJurisdiction = inJurisdiction;
	}
}
