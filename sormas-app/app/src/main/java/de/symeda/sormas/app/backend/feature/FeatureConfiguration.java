package de.symeda.sormas.app.backend.feature;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = FeatureConfiguration.TABLE_NAME)
@DatabaseTable(tableName = FeatureConfiguration.TABLE_NAME)
public class FeatureConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 4027927530101427321L;

	public static final String TABLE_NAME = "featureConfiguration";
	public static final String I18N_PREFIX = "FeatureConfiguration";

	public static final String FEATURE_TYPE = "featureType";
	public static final String DISEASE = "disease";
	public static final String ENABLED = "enabled";
	public static final String END_DATE = "endDate";
	public static final String PROPERTIES_MAP = "propertiesMap";
	public static final String PROPERTIES = "properties";

	@Enumerated(EnumType.STRING)
	private FeatureType featureType;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField
	private boolean enabled;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;

	@Column(name = "properties")
	private String propertiesJson;

	private Map<FeatureTypeProperty, Object> propertiesMap;

	public FeatureType getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public String getPropertiesJson() {
		return propertiesJson;
	}

	public void setPropertiesJson(String propertiesJson) {
		this.propertiesJson = propertiesJson;
	}

	public Map<FeatureTypeProperty, Object> getPropertiesMap() {
		if (propertiesMap == null) {
			propertiesMap = parsePropertiesJson(propertiesJson);
		}
		return propertiesMap;
	}

	private static Map<FeatureTypeProperty, Object> parsePropertiesJson(String propertiesJson) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<FeatureTypeProperty, Object>>() {
		}.getType();
		return gson.fromJson(propertiesJson, type);
	}

	public void setPropertiesMap(Map<FeatureTypeProperty, Object> propertiesMap) {
		if (propertiesMap != null) {
			Gson gson = new Gson();
			propertiesJson = gson.toJson(propertiesMap);
		}
		this.propertiesMap = propertiesMap;
	}
}
