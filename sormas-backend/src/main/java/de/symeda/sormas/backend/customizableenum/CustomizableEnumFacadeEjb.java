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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "CustomizableEnumFacade")
public class CustomizableEnumFacadeEjb implements CustomizableEnumFacade {

	/**
	 * Maps a customizable enum type to all enum value entities of that type in the database.
	 */
	private static final Map<CustomizableEnumType, List<CustomizableEnumValue>> customizableEnumValueEntitiesByType = new HashMap<>();
	/**
	 * Maps a customizable enum type to all enum value strings of that type in the database.
	 */
	private static final Map<CustomizableEnumType, List<String>> customizableEnumValuesByType = new HashMap<>();
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all languages for which translations exist to
	 * the possible enum values of this type, which then finally map to their translated captions.
	 */
	private static final Map<Class<? extends CustomizableEnum>, Map<Language, Map<String, String>>> enumValuesByLanguage = new HashMap<>();
	/**
	 * Maps a customizable enum type (defined by its class) to a map which in turn maps all diseases that are relevant for this enum type
	 * to all enum values that are used for the disease.
	 */
	private static final Map<Class<? extends CustomizableEnum>, Map<Disease, List<String>>> enumValuesByDisease = new HashMap<>();
	/**
	 * Maps a customizable enum type to a map with all enum values of this type as its keys and the properties defined for these
	 * enum values as its values.
	 */
	private static final Map<CustomizableEnumType, Map<String, Map<String, Object>>> enumProperties = new HashMap<>();

	@EJB
	private CustomizableEnumValueService service;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public List<CustomizableEnumValueDto> getAllAfter(Date date) {
		return service.getAllAfter(date, null).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<CustomizableEnumValueDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value) {
		if (!customizableEnumValuesByType.get(type).contains(value)) {
			throw new RuntimeException("Invalid enum value " + value + " for customizable enum type " + type.toString());
		}

		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		// Build caches according to language with no disease association if they're not initialized
		if (!CustomizableEnumFacadeEjb.enumValuesByLanguage.containsKey(enumClass)
			|| !CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).containsKey(language)) {
			getEnumValues(type, null);
		}

		try {
			T enumValue = enumClass.newInstance();
			enumValue.setValue(value);
			enumValue.setCaption(CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).get(language).get(value));
			enumValue.setProperties(enumProperties.get(type).get(value));
			return enumValue;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease) {
		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		CustomizableEnumFacadeEjb.enumValuesByLanguage.putIfAbsent(enumClass, new HashMap<>());
		CustomizableEnumFacadeEjb.enumValuesByDisease.putIfAbsent(enumClass, new HashMap<>());

		if (!CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).containsKey(language)) {
			CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).put(language, new HashMap<>());
			for (CustomizableEnumValue customizableEnumValue : CustomizableEnumFacadeEjb.customizableEnumValueEntitiesByType.get(type)) {
				if (StringUtils.equals(configFacade.getCountryLocale(), language.getLocale().toString())
					|| CollectionUtils.isEmpty(customizableEnumValue.getTranslations())) {
					// If the enum value does not have any translations or the user uses the server language,
					// add the server language to the cache and use the default caption of the enum value
					CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass)
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
						CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass)
							.get(language)
							.putIfAbsent(customizableEnumValue.getValue(), translation.get().getValue());
					} else {
						CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass)
							.get(language)
							.putIfAbsent(customizableEnumValue.getValue(), customizableEnumValue.getCaption());
					}
				}
			}
		}

		// Always add values for no disease because they are relevant in all cases
		if (!CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).containsKey(null)) {
			addValuesByDisease(type, enumClass, null);
		}

		if (!CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).containsKey(disease)) {
			addValuesByDisease(type, enumClass, disease);
		}

		List<T> enumValues = new ArrayList<>();
		CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).get(language).forEach((value, caption) -> {
			try {
				T enumValue = enumClass.newInstance();
				enumValue.setValue(value);
				enumValue.setCaption(caption);
				enumValue.setProperties(enumProperties.get(type).get(value));
				enumValues.add(enumValue);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});

		return enumValues.stream()
			.filter(
				e -> CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).get(disease).contains(e.getValue())
					|| CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).get(null).contains(e.getValue()))
			.collect(Collectors.toList());
	}

	@PostConstruct
	@Override
	public void loadData() {
		CustomizableEnumFacadeEjb.customizableEnumValueEntitiesByType.clear();
		CustomizableEnumFacadeEjb.customizableEnumValuesByType.clear();
		CustomizableEnumFacadeEjb.enumValuesByLanguage.clear();
		CustomizableEnumFacadeEjb.enumValuesByDisease.clear();
		CustomizableEnumFacadeEjb.enumProperties.clear();

		for (CustomizableEnumType enumType : CustomizableEnumType.values()) {
			CustomizableEnumFacadeEjb.customizableEnumValueEntitiesByType.putIfAbsent(enumType, new ArrayList<>());
			CustomizableEnumFacadeEjb.customizableEnumValuesByType.putIfAbsent(enumType, new ArrayList<>());
		}

		// Build list of customizable enums mapped by their enum type; other caches are built on-demand
		for (CustomizableEnumValue customizableEnumValue : service.getAll()) {
			CustomizableEnumType enumType = customizableEnumValue.getDataType();
			CustomizableEnumFacadeEjb.customizableEnumValueEntitiesByType.get(enumType).add(customizableEnumValue);
			String value = customizableEnumValue.getValue();
			if (customizableEnumValuesByType.get(enumType).contains(value)) {
				throw new RuntimeException("Enum value " + value + " for customizable enum type " + enumType.toString() + " is not unique");
			}
			CustomizableEnumFacadeEjb.customizableEnumValuesByType.get(enumType).add(value);
			CustomizableEnumFacadeEjb.enumProperties.putIfAbsent(enumType, new HashMap<>());
			CustomizableEnumFacadeEjb.enumProperties.get(enumType)
				.putIfAbsent(customizableEnumValue.getValue(), customizableEnumValue.getProperties());
		}
	}

	public CustomizableEnumValueDto toDto(CustomizableEnumValue source) {

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

	public CustomizableEnumValue fromDto(@NotNull CustomizableEnumValueDto source, boolean checkChangeDate) {

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
		CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).put(disease, new ArrayList<>());
		List<String> filteredEnumValues = CustomizableEnumFacadeEjb.customizableEnumValueEntitiesByType.get(type)
			.stream()
			.filter(e -> disease == null && CollectionUtils.isEmpty(e.getDiseases()) || e.getDiseases() != null && e.getDiseases().contains(disease))
			.map(CustomizableEnumValue::getValue)
			.collect(Collectors.toList());
		CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).get(disease).addAll(filteredEnumValues);
	}

	@LocalBean
	@Stateless
	public static class CustomizableEnumFacadeEjbLocal extends CustomizableEnumFacadeEjb {

	}

}
