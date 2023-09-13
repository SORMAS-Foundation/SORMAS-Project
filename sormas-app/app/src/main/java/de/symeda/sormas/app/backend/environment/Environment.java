package de.symeda.sormas.app.backend.environment;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentInfrastructureDetails;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.EnumMapKeySerializer;

@Entity(name = Environment.TABLE_NAME)
@DatabaseTable(tableName = Environment.TABLE_NAME)
public class Environment extends PseudonymizableAdo {

	public static final String TABLE_NAME = "environments";
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
	public static final String WATER_USE = "wateruse";
	public static final String OTHER_WATER_USE = "otherWaterUse";
	public static final String LOCATION = "location";

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDate;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "reportingUser_id")
	private User reportingUser;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String environmentName;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String description;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String externalId;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "responsibleUser_id")
	private User responsibleUser;
	@Enumerated(EnumType.STRING)
	private InvestigationStatus investigationStatus;
	@Enumerated(EnumType.STRING)
	private EnvironmentMedia environmentMedia;
	@Enumerated(EnumType.STRING)
	private WaterType waterType;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String otherWaterType;
	@Enumerated(EnumType.STRING)
	private EnvironmentInfrastructureDetails infrastructureDetails;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String otherInfrastructureDetails;
	@Column(name = "waterUse", length = 1024)
	private String waterUseJson;
	private Map<WaterUse, Boolean> wateruse;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String otherWaterUse;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location location;

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

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

	public String getWaterUseJson() {
		return waterUseJson;
	}

	public void setWaterUseJson(String waterUseJson) {
		this.waterUseJson = waterUseJson;
	}

	public Map<WaterUse, Boolean> getWateruse() {
		if (wateruse == null) {
			Gson gson = getGson();
			Type type = new TypeToken<Map<WaterUse, Boolean>>() {
			}.getType();
			wateruse = gson.fromJson(waterUseJson, type);
			if (wateruse == null) {
				wateruse = new HashMap<>();
			}
		}
		return wateruse;
	}

	public void setWateruse(Map<WaterUse, Boolean> wateruse) {
		this.wateruse = wateruse;
		Gson gson = getGson();
		Type type = new TypeToken<Map<WaterUse, Boolean>>() {
		}.getType();
		String waterUseJson1 = gson.toJson(wateruse, type);
		waterUseJson = waterUseJson1;
	}

	@NonNull
	private static Gson getGson() {
		return new GsonBuilder().enableComplexMapKeySerialization().registerTypeAdapter(WaterUse.class, new EnumMapKeySerializer<>(WaterUse.class)).create();
	}

	public String getOtherWaterUse() {
		return otherWaterUse;
	}

	public void setOtherWaterUse(String otherWaterUse) {
		this.otherWaterUse = otherWaterUse;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
