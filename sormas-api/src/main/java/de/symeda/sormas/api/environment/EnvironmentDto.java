package de.symeda.sormas.api.environment;

import java.util.Date;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.ENVIRONMENT_MANAGEMENT)
public class EnvironmentDto extends PseudonymizableDto {

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 2638;

	public static final String I18N_PREFIX = "Environment";

	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String ENVIRONMENT_NAME = "environmentName";
	public static final String DESCRIPTION = "description";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String ENVIRONMENT_MEDIA = "environmentMedia";
	public static final String WATER_TYPE = "waterType";
	public static final String OTHER_WATER_TYPE = "otherWaterType";
	public static final String INFRASTUCTURE_DETAILS = "infrastructureDetails";
	public static final String OTHER_INFRASTRUCTUIRE_DETAILS = "otherInfrastructureDetails";
	public static final String WATER_USE = "waterUse";
	public static final String OTHER_WATER_USE = "otherWaterUse";
	public static final String LOCATION = "location";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	@NotNull(message = Validations.validReportingUser)
	private UserReferenceDto reportingUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@NotBlank(message = Validations.environmentName)
	private String environmentName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String description;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	private UserReferenceDto responsibleUser;
	@NotNull
	private InvestigationStatus investigationStatus;
	@NotNull(message = Validations.environmentMedia)
	private EnvironmentMedia environmentMedia;
	private WaterType waterType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherWaterType;
	private EnvironmentInfrastructureDetails infrastructureDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherInfrastructureDetails;
	private Map<WaterUse, Boolean> waterUse;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherWaterUse;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Valid
	private LocationDto location;

	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static EnvironmentDto build() {
		final EnvironmentDto environment = new EnvironmentDto();
		environment.setUuid(DataHelper.createUuid());
		environment.setReportDate(new Date());
		environment.setLocation(LocationDto.build());
		environment.setInvestigationStatus(InvestigationStatus.PENDING);

		return environment;
	}

	public static EnvironmentDto build(UserDto currentUser) {
		EnvironmentDto environment = build();
		environment.setReportingUser(currentUser.toReference());
		environment.getLocation().setRegion(currentUser.getRegion());
		environment.getLocation().setDistrict(currentUser.getDistrict());

		return environment;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public EnvironmentMedia getEnvironmentMedia() {
		return environmentMedia;
	}

	public void setEnvironmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
	}

	public WaterType getWaterType() {
		return waterType;
	}

	public void setWaterType(WaterType waterType) {
		this.waterType = waterType;
	}

	public String getOtherWaterType() {
		return otherWaterType;
	}

	public void setOtherWaterType(String otherWaterType) {
		this.otherWaterType = otherWaterType;
	}

	public EnvironmentInfrastructureDetails getInfrastructureDetails() {
		return infrastructureDetails;
	}

	public void setInfrastructureDetails(EnvironmentInfrastructureDetails infrastructureDetails) {
		this.infrastructureDetails = infrastructureDetails;
	}

	public String getOtherInfrastructureDetails() {
		return otherInfrastructureDetails;
	}

	public void setOtherInfrastructureDetails(String otherInfrastructureDetails) {
		this.otherInfrastructureDetails = otherInfrastructureDetails;
	}

	public Map<WaterUse, Boolean> getWaterUse() {
		return waterUse;
	}

	public void setWaterUse(Map<WaterUse, Boolean> waterUse) {
		this.waterUse = waterUse;
	}

	public String getOtherWaterUse() {
		return otherWaterUse;
	}

	public void setOtherWaterUse(String otherWaterUse) {
		this.otherWaterUse = otherWaterUse;
	}

	public LocationDto getLocation() {
		return location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
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

	public EnvironmentReferenceDto toReference() {
		return new EnvironmentReferenceDto(getUuid(), getEnvironmentName());
	}
}
