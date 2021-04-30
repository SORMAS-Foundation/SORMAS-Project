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
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.customizableenum.EnumTranslation;
import de.symeda.sormas.api.customizableenum.enumtypes.AbstractEnumValue;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless(name = "CustomizableEnumFacade")
public class CustomizableEnumFacadeEjb implements CustomizableEnumFacade {

	Map<CustomizableEnumType, List<CustomizableEnum>> customizableEnumsByType = new HashMap<>();
	Map<Class<? extends AbstractEnumValue>, Map<Language, Map<String, String>>> enumValuesByLanguage = new HashMap<>();
	Map<Class<? extends AbstractEnumValue>, Map<Disease, List<String>>> enumValuesByDisease = new HashMap<>();

	@EJB
	private CustomizableEnumService service;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractEnumValue> T getEnumValue(CustomizableEnumType type, String value) {
		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		enumValuesByLanguage.putIfAbsent(enumClass, new HashMap<>());
		enumValuesByLanguage.get(enumClass).putIfAbsent(language, new HashMap<>());

		if (enumValuesByLanguage.get(enumClass).get(language).isEmpty()) {
			getEnumValues(type, null);
		}

		T enumValue;
		try {
			enumValue = enumClass.newInstance();
			enumValue.setValue(value);
			enumValue.setCaption(enumValuesByLanguage.get(enumClass).get(language).get(value));
			return enumValue;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractEnumValue> List<T> getEnumValues(CustomizableEnumType type, Disease disease) {
		Language language = I18nProperties.getUserLanguage();
		Class<T> enumClass = (Class<T>) type.getEnumClass();

		enumValuesByLanguage.putIfAbsent(enumClass, new HashMap<>());
		enumValuesByLanguage.get(enumClass).putIfAbsent(language, new HashMap<>());
		enumValuesByDisease.putIfAbsent(enumClass, new HashMap<>());
		enumValuesByDisease.get(enumClass).putIfAbsent(disease, new ArrayList<>());

		if (enumValuesByLanguage.get(enumClass).get(language).isEmpty()) {
			for (CustomizableEnum customizableEnum : customizableEnumsByType.get(type)) {
				if (StringUtils.equals(configFacade.getCountryLocale(), language.getLocale().toString())
					|| CollectionUtils.isEmpty(customizableEnum.getTranslations())) {
					enumValuesByLanguage.get(enumClass).get(language).putIfAbsent(customizableEnum.getValue(), customizableEnum.getCaption());
				} else {
					Optional<EnumTranslation> translation = customizableEnum.getTranslations()
						.stream()
						.filter(t -> t.getLanguageCode().equals(language.getLocale().toString()))
						.findFirst();
					if (translation.isPresent()) {
						enumValuesByLanguage.get(enumClass).get(language).putIfAbsent(customizableEnum.getValue(), translation.get().getValue());
					} else {
						enumValuesByLanguage.get(enumClass).get(language).putIfAbsent(customizableEnum.getValue(), customizableEnum.getCaption());
					}
				}
			}
		}

		if (enumValuesByDisease.get(enumClass).get(disease).isEmpty()) {
			List<String> filteredEnumValues = customizableEnumsByType.get(type)
				.stream()
				.filter(
					e -> disease == null && CollectionUtils.isEmpty(e.getDiseases()) || e.getDiseases() != null && e.getDiseases().contains(disease))
				.map(CustomizableEnum::getValue)
				.collect(Collectors.toList());
			enumValuesByDisease.get(enumClass).get(disease).addAll(filteredEnumValues);
		}

		List<T> enumValues = new ArrayList<>();
		enumValuesByLanguage.get(enumClass).get(language).forEach((value, caption) -> {
			T enumValue;
			try {
				enumValue = enumClass.newInstance();
				enumValue.setValue(value);
				enumValue.setCaption(caption);
				enumValues.add(enumValue);
			} catch (InstantiationException | IllegalAccessException e) {
				// Do nothing
			}
		});

		return enumValues.stream().filter(e -> enumValuesByDisease.get(enumClass).get(disease).contains(e.getValue())).collect(Collectors.toList());
	}

	@PostConstruct
	@Override
	public void loadData() {
		// Reset caches
		customizableEnumsByType.clear();
		enumValuesByLanguage.clear();
		enumValuesByDisease.clear();

		// Build list of customizable enums mapped by their enum type
		for (CustomizableEnum customizableEnum : service.getAll()) {
			CustomizableEnumType enumType = customizableEnum.getDataType();
			customizableEnumsByType.putIfAbsent(enumType, new ArrayList<>());
			customizableEnumsByType.get(enumType).add(customizableEnum);
		}
	}

	@LocalBean
	@Stateless
	public static class CustomizableEnumFacadeEjbLocal extends CustomizableEnumFacadeEjb {

	}

}
