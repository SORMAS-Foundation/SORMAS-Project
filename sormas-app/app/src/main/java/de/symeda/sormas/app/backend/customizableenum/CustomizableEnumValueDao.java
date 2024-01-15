/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.customizableenum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;

public class CustomizableEnumValueDao extends AbstractAdoDao<CustomizableEnumValue> {

	/**
	 * Maps a customizable enum type to all enum values of that type in the database.
	 */
	private final Map<CustomizableEnumType, List<CustomizableEnumValue>> customizableEnumsByType = new HashMap<>();
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all languages for which translations exist to
	 * the possible enum values of this type, which then finally map to their translated captions.
	 */
	private final Map<Class<? extends CustomizableEnum>, Map<Language, Map<String, String>>> enumValuesByLanguage = new HashMap<>();
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all diseases that are relevant for this enum type
	 * to all enum values that are used for the disease.
	 */
	private final Map<Class<? extends CustomizableEnum>, Map<Disease, List<String>>> enumValuesByDisease = new HashMap<>();
	/**
	 * Maps a customizable enum type to a map with all enum values of this type as its keys and and info, e.g. properties and active status,
	 * defined for these enum values as its values.
	 */
	private final Map<CustomizableEnumType, Map<String, CustomizableEnumCacheInfo>> enumInfo = new HashMap<>();

	public CustomizableEnumValueDao(Dao<CustomizableEnumValue, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<CustomizableEnumValue> getAdoClass() {
		return CustomizableEnumValue.class;
	}

	@Override
	public String getTableName() {
		return CustomizableEnumValue.TABLE_NAME;
	}

	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value) {
		if (customizableEnumsByType.isEmpty()) {
			loadData();
		}

		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		initCaches(type, language);

		try {
			T enumValue = enumClass.newInstance();
			enumValue.setValue(value);
			enumValue.setCaption(enumValuesByLanguage.get(enumClass).get(language).get(value));
			enumValue.setProperties(getEnumInfo(type, value).getProperties());
			return enumValue;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease) {
		return getEnumValues(type, null, disease);
	}

	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, String selectedValue, Disease disease) {

		if (customizableEnumsByType.isEmpty() || (long) customizableEnumsByType.values().stream().mapToInt(i -> i.size()).sum() != countOf()) {
			loadData();
		}

		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		initCaches(type, language);

		// Always add values for no disease because they are relevant in all cases
		if (!enumValuesByDisease.get(enumClass).containsKey(null)) {
			addValuesByDisease(type, enumClass, null);
		}

		if (!enumValuesByDisease.get(enumClass).containsKey(disease)) {
			addValuesByDisease(type, enumClass, disease);
		}

		List<T> enumValues = new ArrayList<>();
		enumValuesByLanguage.get(enumClass).get(language).forEach((value, caption) -> {
			T enumValue;
			try {
				enumValue = enumClass.newInstance();
				enumValue.setValue(value);
				enumValue.setCaption(caption);
				enumValue.setProperties(getEnumInfo(type, value).getProperties());
				enumValues.add(enumValue);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});

		return enumValues.stream()
			.filter(
				e -> (getEnumInfo(type, e.getValue()).isActive() || (selectedValue != null && selectedValue.equals(e.getValue())))
					&& (enumValuesByDisease.get(enumClass).get(disease).contains(e.getValue())
						|| enumValuesByDisease.get(enumClass).get(null).contains(e.getValue())))
			.collect(Collectors.toList());
	}

	public boolean hasEnumValues(CustomizableEnumType type, Disease disease) {
		return !getEnumValues(type, disease).isEmpty();
	}

	private <T extends CustomizableEnum> void initCaches(CustomizableEnumType type, Language language) {
		Class<T> enumClass = (Class<T>) type.getEnumClass();
		if (enumValuesByLanguage.containsKey(enumClass) && enumValuesByLanguage.get(enumClass).containsKey(language)) {
			return;
		}

		// Build caches according to language with no disease association if they're not initialized
		enumValuesByLanguage.putIfAbsent(enumClass, new HashMap<>());
		enumValuesByDisease.putIfAbsent(enumClass, new HashMap<>());

		if (!enumValuesByLanguage.get(enumClass).containsKey(language)) {
			enumValuesByLanguage.get(enumClass).put(language, new HashMap<>());
			for (CustomizableEnumValue customizableEnumValue : customizableEnumsByType.get(type)) {
				if (customizableEnumValue.isDefaultValue()) {
					// Default values use translations provided in the properties files
					String caption =
						I18nProperties.getEnumCaption(language, customizableEnumValue.getDataType().toString(), customizableEnumValue.getValue());

					if (StringUtils.isBlank(caption)) {
						caption = customizableEnumValue.getCaption();
					}

					enumValuesByLanguage.get(enumClass).get(language).putIfAbsent(customizableEnumValue.getValue(), caption);
				} else if (StringUtils.equals(ConfigProvider.getServerLocale(), language.getLocale().toString())
					|| CollectionUtils.isEmpty(customizableEnumValue.getTranslations())) {
					// If the enum value does not have any translations or the user uses the server language,
					// add the server language to the cache and use the default caption of the enum value
					enumValuesByLanguage.get(enumClass)
						.get(language)
						.putIfAbsent(customizableEnumValue.getValue(), customizableEnumValue.getCaption());
				} else {
					// Check whether the list of translations contains the user language; if yes, add that language
					// to the cache and use its translation; if not, fall back to the default caption of the enum value
					Optional<CustomizableEnumTranslation> translation = customizableEnumValue.getTranslations()
						.stream()
						.filter(t -> t.getLanguageCode().equals(language.getLocale().toString()))
						.findFirst();
					if (translation.isPresent()) {
						enumValuesByLanguage.get(enumClass).get(language).putIfAbsent(customizableEnumValue.getValue(), translation.get().getValue());
					} else {
						enumValuesByLanguage.get(enumClass)
							.get(language)
							.putIfAbsent(customizableEnumValue.getValue(), customizableEnumValue.getCaption());
					}
				}
			}
		}
	}

	private <T extends CustomizableEnum> void addValuesByDisease(CustomizableEnumType type, Class<T> enumClass, Disease disease) {
		enumValuesByDisease.get(enumClass).put(disease, new ArrayList<>());
		List<String> filteredEnumValues = customizableEnumsByType.get(type)
			.stream()
			.filter(e -> disease == null && CollectionUtils.isEmpty(e.getDiseases()) || e.getDiseases() != null && e.getDiseases().contains(disease))
			.map(CustomizableEnumValue::getValue)
			.collect(Collectors.toList());
		enumValuesByDisease.get(enumClass).get(disease).addAll(filteredEnumValues);
	}

	private void loadData() {
		customizableEnumsByType.clear();
		enumValuesByLanguage.clear();
		enumValuesByDisease.clear();
		enumInfo.clear();

		for (CustomizableEnumType enumType : CustomizableEnumType.values()) {
			customizableEnumsByType.putIfAbsent(enumType, new ArrayList<>());
		}

		for (CustomizableEnumValue customizableEnumValue : queryForAll()) {
			CustomizableEnumType enumType = customizableEnumValue.getDataType();
			customizableEnumsByType.get(enumType).add(customizableEnumValue);
			enumInfo.putIfAbsent(enumType, new HashMap<>());
			enumInfo.get(enumType)
				.putIfAbsent(
					customizableEnumValue.getValue(),
					new CustomizableEnumCacheInfo(customizableEnumValue.getProperties(), customizableEnumValue.isActive()));
		}
	}

	private CustomizableEnumCacheInfo getEnumInfo(CustomizableEnumType type, String value) {
		return enumInfo.get(type).get(value);
	}

}
