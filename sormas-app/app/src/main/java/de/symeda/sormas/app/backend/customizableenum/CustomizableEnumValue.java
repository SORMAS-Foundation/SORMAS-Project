package de.symeda.sormas.app.backend.customizableenum;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = CustomizableEnumValue.TABLE_NAME)
@DatabaseTable(tableName = CustomizableEnumValue.TABLE_NAME)
public class CustomizableEnumValue extends AbstractDomainObject {

	public static final String TABLE_NAME = "customizableEnumValue";
	public static final String I18N_PREFIX = "CustomizableEnum";

	@Enumerated(EnumType.STRING)
	private CustomizableEnumType dataType;

	@Column
	private String value;

	@Column
	private String caption;

	@Column(name = "translations")
	private String translationsJson;
	private List<CustomizableEnumTranslation> translations;

	@Column(name = "diseases")
	private String diseasesString;
	private Set<Disease> diseases;

	@Column
	private String description;

	@DatabaseField
	private boolean defaultValue;

	@Column(name = "descriptionTranslations")
	private String descriptionTranslationsJson;
	private List<CustomizableEnumTranslation> descriptionTranslations;

	@Column(name = "properties")
	private String propertiesJson;
	private Map<String, Object> properties;

	@DatabaseField
	private boolean active;

	public CustomizableEnumType getDataType() {
		return dataType;
	}

	public void setDataType(CustomizableEnumType dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@JsonRawValue
	public String getTranslationsJson() {
		return translationsJson;
	}

	public void setTranslationsJson(String translationsJson) {
		this.translationsJson = translationsJson;
		this.translations = null;
	}

	@Transient
	public List<CustomizableEnumTranslation> getTranslations() {
		if (translations == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<CustomizableEnumTranslation>>() {
			}.getType();
			translations = gson.fromJson(translationsJson, type);
			if (translations == null) {
				translations = new ArrayList<>();
			}
		}

		return translations;
	}

	public void setTranslations(List<CustomizableEnumTranslation> translations) {
		this.translations = translations;
		Gson gson = new Gson();
		translationsJson = gson.toJson(translations);
	}

	public String getDiseasesString() {
		return diseasesString;
	}

	public void setDiseasesString(String diseasesString) {
		this.diseasesString = diseasesString;
		this.diseases = null;
	}

	@Transient
	public Set<Disease> getDiseases() {
		if (diseases == null) {
			if (StringUtils.isBlank(diseasesString)) {
				diseases = new HashSet<>();
			} else {
				diseases = Stream.of(diseasesString.split(",")).map(Disease::valueOf).collect(Collectors.toSet());
			}
		}

		return diseases;
	}

	public void setDiseases(Set<Disease> diseases) {
		this.diseases = diseases;
		if (CollectionUtils.isNotEmpty(diseases)) {
			diseasesString = diseases.stream().map(Disease::getName).collect(Collectors.joining(","));
		} else {
			diseasesString = null;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonRawValue
	public String getDescriptionTranslationsJson() {
		return descriptionTranslationsJson;
	}

	public void setDescriptionTranslationsJson(String descriptionTranslationsJson) {
		this.descriptionTranslationsJson = descriptionTranslationsJson;
		this.descriptionTranslations = null;
	}

	@Transient
	public List<CustomizableEnumTranslation> getDescriptionTranslations() {
		if (descriptionTranslations == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<CustomizableEnumTranslation>>() {
			}.getType();
			descriptionTranslations = gson.fromJson(descriptionTranslationsJson, type);
			if (descriptionTranslations == null) {
				descriptionTranslations = new ArrayList<>();
			}
		}

		return descriptionTranslations;
	}

	public void setDescriptionTranslations(List<CustomizableEnumTranslation> descriptionTranslations) {
		this.descriptionTranslations = descriptionTranslations;
		Gson gson = new Gson();
		descriptionTranslationsJson = gson.toJson(descriptionTranslations);
	}

	@JsonRawValue
	public String getPropertiesJson() {
		return propertiesJson;
	}

	public void setPropertiesJson(String propertiesJson) {
		this.propertiesJson = propertiesJson;
		this.properties = null;
	}

	@Transient
	public Map<String, Object> getProperties() {
		if (properties == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			properties = gson.fromJson(propertiesJson, type);
			if (properties == null) {
				properties = new HashMap<>();
			}
		}

		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		Gson gson = new Gson();
		propertiesJson = gson.toJson(properties);
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
