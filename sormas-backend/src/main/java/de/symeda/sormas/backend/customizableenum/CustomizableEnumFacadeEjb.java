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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless(name = "CustomizableEnumFacade")
public class CustomizableEnumFacadeEjb implements CustomizableEnumFacade {

	/**
	 * Maps a customizable enum type to all enum values of that type in the database.
	 */
	private static final Map<CustomizableEnumType, List<CustomizableEnumValue>> customizableEnumsByType = new HashMap<>();
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

	@EJB
	private CustomizableEnumValueService service;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value) {
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
			for (CustomizableEnumValue customizableEnumValue : CustomizableEnumFacadeEjb.customizableEnumsByType.get(type)) {
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

		if (!CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).containsKey(disease)) {
			CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).put(disease, new ArrayList<>());
			List<String> filteredEnumValues = CustomizableEnumFacadeEjb.customizableEnumsByType.get(type)
				.stream()
				.filter(
					e -> disease == null && CollectionUtils.isEmpty(e.getDiseases()) || e.getDiseases() != null && e.getDiseases().contains(disease))
				.map(CustomizableEnumValue::getValue)
				.collect(Collectors.toList());
			CustomizableEnumFacadeEjb.enumValuesByDisease.get(enumClass).get(disease).addAll(filteredEnumValues);
		}

		List<T> enumValues = new ArrayList<>();
		CustomizableEnumFacadeEjb.enumValuesByLanguage.get(enumClass).get(language).forEach((value, caption) -> {
			T enumValue;
			try {
				enumValue = enumClass.newInstance();
				enumValue.setValue(value);
				enumValue.setCaption(caption);
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
		CustomizableEnumFacadeEjb.customizableEnumsByType.clear();
		CustomizableEnumFacadeEjb.enumValuesByLanguage.clear();
		CustomizableEnumFacadeEjb.enumValuesByDisease.clear();

		// Build list of customizable enums mapped by their enum type; other caches are built on-demand
		for (CustomizableEnumValue customizableEnumValue : service.getAll()) {
			CustomizableEnumType enumType = customizableEnumValue.getDataType();
			CustomizableEnumFacadeEjb.customizableEnumsByType.putIfAbsent(enumType, new ArrayList<>());
			CustomizableEnumFacadeEjb.customizableEnumsByType.get(enumType).add(customizableEnumValue);
		}
	}

	@LocalBean
	@Stateless
	public static class CustomizableEnumFacadeEjbLocal extends CustomizableEnumFacadeEjb {

	}

}
