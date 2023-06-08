package de.symeda.sormas.backend.environment;

import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentInfrastructureDetails;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity(name = "environments")
public class Environment extends CoreAdo {

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
	public static final String WATER_DRINKING_HOUSEHOLD = "waterUseDrinkingHousehold";
	public static final String WATER_USE = "waterUse";
	public static final String OTHER_WATER_USE = "otherWaterUse";
	public static final String LOCATION = "location";

	private Date reportDate;
	private User reportingUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String environmentName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	private User responsibleUser;
	private InvestigationStatus investigationStatus;
	private EnvironmentMedia environmentMedia;
	private WaterType waterType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherWaterType;
	private EnvironmentInfrastructureDetails infrastructureDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherInfrastructureDetails;
	private Map<WaterUse, Boolean> waterUse;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherWaterUse;
	@Valid
	private Location location;

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
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

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(User responsibleUser) {
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

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
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

	@OneToOne(cascade = CascadeType.ALL)
	public Location getLocation() {
		if (location == null) {
			location = new Location();
		}
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
