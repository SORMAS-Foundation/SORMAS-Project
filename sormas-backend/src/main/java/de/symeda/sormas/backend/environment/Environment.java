package de.symeda.sormas.backend.environment;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentInfrastructureDetails;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
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
	private String environmentName;
	private String description;
	private String externalId;
	private User responsibleUser;
	private InvestigationStatus investigationStatus;
	private EnvironmentMedia environmentMedia;
	private WaterType waterType;
	private String otherWaterType;
	private EnvironmentInfrastructureDetails infrastructureDetails;
	private String otherInfrastructureDetails;
	private Map<WaterUse, Boolean> waterUse;
	private String otherWaterUse;
	private Location location;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT, nullable = false)
	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public EnvironmentMedia getEnvironmentMedia() {
		return environmentMedia;
	}

	public void setEnvironmentMedia(EnvironmentMedia environmentMedia) {
		this.environmentMedia = environmentMedia;
	}

	@Enumerated(EnumType.STRING)
	public WaterType getWaterType() {
		return waterType;
	}

	public void setWaterType(WaterType waterType) {
		this.waterType = waterType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getOtherWaterType() {
		return otherWaterType;
	}

	public void setOtherWaterType(String otherWaterType) {
		this.otherWaterType = otherWaterType;
	}

	@Enumerated(EnumType.STRING)
	public EnvironmentInfrastructureDetails getInfrastructureDetails() {
		return infrastructureDetails;
	}

	public void setInfrastructureDetails(EnvironmentInfrastructureDetails infrastructureDetails) {
		this.infrastructureDetails = infrastructureDetails;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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
