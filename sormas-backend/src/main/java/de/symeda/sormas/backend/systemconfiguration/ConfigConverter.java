package de.symeda.sormas.backend.systemconfiguration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.systemconfiguration.Config;

/**
 * Converter was written to handle H2-database, it's fine.
 */
@Converter
public class ConfigConverter implements AttributeConverter<Config, String> {

	@Override
	public String convertToDatabaseColumn(Config attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.name();  // or attribute.toString()
	}

	@Override
	public Config convertToEntityAttribute(String dbData) {
		if (StringUtils.isBlank(dbData)) {
			return null;
		}
		return Config.valueOf(dbData);
	}
}
