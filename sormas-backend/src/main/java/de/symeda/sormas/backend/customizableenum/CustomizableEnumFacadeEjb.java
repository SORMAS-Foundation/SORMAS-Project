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

package de.symeda.sormas.backend.customizableenum;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.InvalidCustomizationException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Singleton(name = "CustomizableEnumFacade")
public class CustomizableEnumFacadeEjb implements CustomizableEnumFacade {

	/**
	 * Maps a customizable enum type to all enum value entities of that type in the database.
	 */
	private final EnumMap<CustomizableEnumType, List<CustomizableEnumValue>> enumValueEntities = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type to all enum value strings of that type in the database.
	 */
	private final EnumMap<CustomizableEnumType, List<String>> enumValues = new EnumMap<>(CustomizableEnumType.class);
	/**
	 * Maps a customizable enum type to a map with all enum values of this type as its keys and the properties defined for these
	 * enum values as its values.
	 */
	private final EnumMap<CustomizableEnumType, Map<String, Map<String, Object>>> enumProperties = new EnumMap<>(CustomizableEnumType.class);
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

	@EJB
	private CustomizableEnumValueService service;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Lock(LockType.READ)
	@Override
	public List<CustomizableEnumValueDto> getAllAfter(Date date) {
		return service.getAllAfter(date, null).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Lock(LockType.READ)
	@Override
	public List<CustomizableEnumValueDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Lock(LockType.READ)
	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	/**
	 * Using LockType.READ to provide concurrent locks to multiple clients.
	 */
	@Lock(LockType.READ)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value) {
		if (!enumValues.get(type).contains(value)) {
			throw new IllegalArgumentException("Invalid enum value " + value + " for customizable enum type " + type.toString());
		}

		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		initCaches(type, language);

		try {
			T enumValue = enumClass.getDeclaredConstructor().newInstance();
			enumValue.setValue(value);
			enumValue.setCaption(enumValuesByLanguage.get(enumClass).get(language).get(value));
			enumValue.setProperties(enumProperties.get(type).get(value));
			return enumValue;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Lock(LockType.READ)
	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease) {
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
			try {
				T enumValue = enumClass.getDeclaredConstructor().newInstance();
				enumValue.setValue(value);
				enumValue.setCaption(caption);
				enumValue.setProperties(enumProperties.get(type).get(value));
				enumValues.add(enumValue);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		});

		return enumValues.stream()
			.filter(
				e -> enumValuesByDisease.get(enumClass).get(disease).contains(e.getValue())
					|| enumValuesByDisease.get(enumClass).get(null).contains(e.getValue()))
			.collect(Collectors.toList());
	}

	@Lock(LockType.READ)
	@Override
	public boolean hasEnumValues(CustomizableEnumType type, Disease disease) {
		return !getEnumValues(type, disease).isEmpty();
	}

	@SuppressWarnings("unchecked")
	private <T extends CustomizableEnum> void initCaches(CustomizableEnumType type, Language language) {
		Class<T> enumClass = (Class<T>) type.getEnumClass();
		if (enumValuesByLanguage.containsKey(enumClass) && enumValuesByLanguage.get(enumClass).containsKey(language)) {
			return;
		}

		// Build caches according to language with no disease association if they're not initialized
		enumValuesByLanguage.putIfAbsent(enumClass, new HashMap<>()); // can't use EnumMap here - we need null values
		enumValuesByDisease.putIfAbsent(enumClass, new HashMap<>());

		if (!enumValuesByLanguage.get(enumClass).containsKey(language)) {
			enumValuesByLanguage.get(enumClass).put(language, new HashMap<>());
			for (CustomizableEnumValue customizableEnumValue : enumValueEntities.get(type)) {
				if (StringUtils.equals(configFacade.getCountryLocale(), language.getLocale().toString())
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

	@PostConstruct
	@Override
	public void loadData() {
		enumValueEntities.clear();
		enumValues.clear();
		enumValuesByLanguage.clear();
		enumValuesByDisease.clear();
		enumProperties.clear();

		for (CustomizableEnumType enumType : CustomizableEnumType.values()) {
			enumValueEntities.putIfAbsent(enumType, new ArrayList<>());
			enumValues.putIfAbsent(enumType, new ArrayList<>());
		}

		// Build list of customizable enums mapped by their enum type; other caches are built on-demand
		for (CustomizableEnumValue customizableEnumValue : service.getAll()) {
			CustomizableEnumType enumType = customizableEnumValue.getDataType();
			enumValueEntities.get(enumType).add(customizableEnumValue);
			String value = customizableEnumValue.getValue();
			if (enumValues.get(enumType).contains(value)) {
				throw new InvalidCustomizationException(
					"Enum value " + value + " for customizable enum type " + enumType.toString() + " is not unique");
			}
			enumValues.get(enumType).add(value);
			enumProperties.putIfAbsent(enumType, new HashMap<>());
			enumProperties.get(enumType).putIfAbsent(customizableEnumValue.getValue(), customizableEnumValue.getProperties());
		}
	}

	private CustomizableEnumValueDto toDto(CustomizableEnumValue source) {

		if (source == null) {
			return null;
		}

		CustomizableEnumValueDto target = new CustomizableEnumValueDto();
		DtoHelper.fillDto(target, source);

		target.setDataType(source.getDataType());
		target.setValue(source.getValue());
		target.setCaption(source.getCaption());
		target.setTranslations(source.getTranslations());
		target.setDiseases(source.getDiseases());
		target.setDescription(source.getDescription());
		target.setDescriptionTranslations(source.getDescriptionTranslations());
		target.setProperties(source.getProperties());

		return target;
	}

	private CustomizableEnumValue fromDto(@NotNull CustomizableEnumValueDto source, boolean checkChangeDate) {

		CustomizableEnumValue target =
			DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), CustomizableEnumValue::new, checkChangeDate);

		target.setDataType(source.getDataType());
		target.setValue(source.getValue());
		target.setCaption(source.getCaption());
		target.setTranslations(source.getTranslations());
		target.setDiseases(source.getDiseases());
		target.setDescription(source.getDescription());
		target.setDescriptionTranslations(source.getDescriptionTranslations());
		target.setProperties(source.getProperties());

		return target;
	}

	private <T extends CustomizableEnum> void addValuesByDisease(CustomizableEnumType type, Class<T> enumClass, Disease disease) {
		enumValuesByDisease.get(enumClass).put(disease, new ArrayList<>());
		List<String> filteredEnumValues = enumValueEntities.get(type)
			.stream()
			.filter(e -> disease == null && CollectionUtils.isEmpty(e.getDiseases()) || e.getDiseases() != null && e.getDiseases().contains(disease))
			.map(CustomizableEnumValue::getValue)
			.collect(Collectors.toList());
		enumValuesByDisease.get(enumClass).get(disease).addAll(filteredEnumValues);
	}
}
