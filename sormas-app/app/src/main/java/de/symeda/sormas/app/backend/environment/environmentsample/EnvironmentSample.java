package de.symeda.sormas.app.backend.environment.environmentsample;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.environment.environmentsample.WeatherCondition;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.customizableenum.CustomizableEnumValueDao;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.EnumMapKeySerializer;

@Entity(name = EnvironmentSample.TABLE_NAME)
@DatabaseTable(tableName = EnvironmentSample.TABLE_NAME)
public class EnvironmentSample extends PseudonymizableAdo {

	public static final String I18N_PREFIX = "EnvironmentSample";

	public static final String TABLE_NAME = "environmentSamples";

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String REQUESTED_PATHOGEN_TESTS = "requestedPathogenTests";
	public static final String WEATHER_CONDITIONS = "weatherConditions";
	public static final String LOCATION = "location";
	public static final String ENVIRONMENT = "environment";
	public static final String DISPATCHED = "dispatched";
	public static final String RECEIVED = "received";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "environment_id")
	private Environment environment;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "reportingUser_id")
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date sampleDateTime;
	@Enumerated(EnumType.STRING)
	private EnvironmentSampleMaterial sampleMaterial;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String otherSampleMaterial;
	@Column
	private Float sampleVolume;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String fieldSampleId;
	@Column
	private Integer turbidity;
	@Column
	private Integer phValue;
	@Column
	private Integer sampleTemperature;
	@Column
	private Float chlorineResiduals;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "laboratory_id")
	private Facility laboratory;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String laboratoryDetails;
	@Column(name = "requestedPathogenTests", length = CHARACTER_LIMIT_TEXT)
	private String requestedPathogenTestsJson;
	private Set<Pathogen> requestedPathogenTests;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String otherRequestedPathogenTests;
	@Column(name = "weatherConditions", length = CHARACTER_LIMIT_TEXT)
	private String weatherConditionsJson;
	private Map<WeatherCondition, Boolean> weatherConditions;
	@Enumerated
	private YesNoUnknown heavyRain;
	@Column
	private boolean dispatched;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dispatchDate;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String dispatchDetails;
	@Column
	private boolean received;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date receivalDate;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String labSampleId;
	@Enumerated
	private SpecimenCondition specimenCondition;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "location_id")
	private Location location;
	@Column(length = CHARACTER_LIMIT_TEXT)
	private String generalComment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public EnvironmentSampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(EnvironmentSampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public String getOtherSampleMaterial() {
		return otherSampleMaterial;
	}

	public void setOtherSampleMaterial(String otherSampleMaterial) {
		this.otherSampleMaterial = otherSampleMaterial;
	}

	public Float getSampleVolume() {
		return sampleVolume;
	}

	public void setSampleVolume(Float sampleVolume) {
		this.sampleVolume = sampleVolume;
	}

	public String getFieldSampleId() {
		return fieldSampleId;
	}

	public void setFieldSampleId(String fieldSampleId) {
		this.fieldSampleId = fieldSampleId;
	}

	public Integer getTurbidity() {
		return turbidity;
	}

	public void setTurbidity(Integer turbidity) {
		this.turbidity = turbidity;
	}

	public Integer getPhValue() {
		return phValue;
	}

	public void setPhValue(Integer phValue) {
		this.phValue = phValue;
	}

	public Integer getSampleTemperature() {
		return sampleTemperature;
	}

	public void setSampleTemperature(Integer sampleTemperature) {
		this.sampleTemperature = sampleTemperature;
	}

	public Float getChlorineResiduals() {
		return chlorineResiduals;
	}

	public void setChlorineResiduals(Float chlorineResiduals) {
		this.chlorineResiduals = chlorineResiduals;
	}

	public Facility getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(Facility laboratory) {
		this.laboratory = laboratory;
	}

	public String getLaboratoryDetails() {
		return laboratoryDetails;
	}

	public void setLaboratoryDetails(String laboratoryDetails) {
		this.laboratoryDetails = laboratoryDetails;
	}

	public String getRequestedPathogenTestsJson() {
		return requestedPathogenTestsJson;
	}

	public void setRequestedPathogenTestsJson(String requestedPathogenTestsJson) {
		this.requestedPathogenTestsJson = requestedPathogenTestsJson;
	}

	public Set<Pathogen> getRequestedPathogenTests() {
		if (requestedPathogenTests == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<String>>() {
			}.getType();
			List<String> list = gson.fromJson(requestedPathogenTestsJson, type);
			CustomizableEnumValueDao customizableEnumValueDao = DatabaseHelper.getCustomizableEnumValueDao();

			requestedPathogenTests = list != null
				? list.stream()
					.map(p -> customizableEnumValueDao.<Pathogen> getEnumValue(CustomizableEnumType.PATHOGEN, p))
					.collect(Collectors.toSet())
				: new HashSet<>();
		}

		return requestedPathogenTests;
	}

	public void setRequestedPathogenTests(Set<Pathogen> requestedPathogenTests) {
		if (requestedPathogenTests == null) {
			this.requestedPathogenTests = null;
			this.requestedPathogenTestsJson = null;
		} else {
			this.requestedPathogenTests = requestedPathogenTests;
			Gson gson = new Gson();
			Type type = new TypeToken<List<String>>() {
			}.getType();

			requestedPathogenTestsJson =
				gson.toJson(requestedPathogenTests.stream().map(CustomizableEnum::getValue).collect(Collectors.toSet()), type);
		}
	}

	public String getOtherRequestedPathogenTests() {
		return otherRequestedPathogenTests;
	}

	public void setOtherRequestedPathogenTests(String otherRequestedPathogenTests) {
		this.otherRequestedPathogenTests = otherRequestedPathogenTests;
	}

	public String getWeatherConditionsJson() {
		return weatherConditionsJson;
	}

	public void setWeatherConditionsJson(String weatherConditionsJson) {
		this.weatherConditionsJson = weatherConditionsJson;
	}

	public Map<WeatherCondition, Boolean> getWeatherConditions() {
		if (weatherConditions == null) {
			Gson gson = getGsonForWeatherConditions();
			Type type = new TypeToken<Map<WeatherCondition, Boolean>>() {
			}.getType();

			weatherConditions = gson.fromJson(weatherConditionsJson, type);
			if (weatherConditions == null) {
				weatherConditions = new HashMap<>();
			}
		}

		return weatherConditions;
	}

	public void setWeatherConditions(Map<WeatherCondition, Boolean> weatherConditions) {
		this.weatherConditions = weatherConditions;
		Gson gson = getGsonForWeatherConditions();
		Type type = new TypeToken<Map<WeatherCondition, Boolean>>() {
		}.getType();

		weatherConditionsJson = gson.toJson(weatherConditions, type);

	}

	public YesNoUnknown getHeavyRain() {
		return heavyRain;
	}

	public void setHeavyRain(YesNoUnknown heavyRain) {
		this.heavyRain = heavyRain;
	}

	public boolean isDispatched() {
		return dispatched;
	}

	public void setDispatched(boolean dispatched) {
		this.dispatched = dispatched;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getDispatchDetails() {
		return dispatchDetails;
	}

	public void setDispatchDetails(String dispatchDetails) {
		this.dispatchDetails = dispatchDetails;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public Date getReceivalDate() {
		return receivalDate;
	}

	public void setReceivalDate(Date receivalDate) {
		this.receivalDate = receivalDate;
	}

	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getGeneralComment() {
		return generalComment;
	}

	public void setGeneralComment(String generalComment) {
		this.generalComment = generalComment;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	private static Gson getGsonForWeatherConditions() {
		return new GsonBuilder().enableComplexMapKeySerialization()
			.registerTypeAdapter(WeatherCondition.class, new EnumMapKeySerializer<>(WeatherCondition.class))
			.create();
	}
}
