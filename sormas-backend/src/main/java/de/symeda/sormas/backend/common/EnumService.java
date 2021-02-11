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

package de.symeda.sormas.backend.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.UserService;

@Singleton(name = "EnumService")
@RunAs(UserRole._SYSTEM)
public class EnumService {

	private final Map<Class<Enum>, Map<Language, EnumCaption>> enumCaptions = new HashMap<>();

	@EJB
	private UserService userService;

	public Enum<?> getEnumByCaption(Class<Enum> enumType, String caption) throws InvalidEnumCaptionException {
		Language language = userService.getCurrentUser().getLanguage();

		if (!enumCaptions.containsKey(enumType)) {
			addCaptions(enumCaptions, enumType);
		}

		Map<Language, EnumCaption> captions = enumCaptions.get(enumType);
		if (!captions.containsKey(language)) {
			addCaptions(enumCaptions, enumType);
		}

		return captions.get(language).getEnumByCaption(caption);
	}

	private synchronized void addCaptions(Map<Class<Enum>, Map<Language, EnumCaption>> enumCaptions, Class<Enum> enumClass) {
		Language language = userService.getCurrentUser().getLanguage();

		Map<Language, EnumCaption> captions = enumCaptions.getOrDefault(enumClass, new HashMap<>());

		Map<Enum<?>, String> languageCaptions = new HashMap<>();
		for (Enum<?> item : enumClass.getEnumConstants()) {
			languageCaptions.put(item, I18nProperties.getEnumCaption(language, item));
		}

		captions.put(language, new EnumCaption(languageCaptions));
		enumCaptions.put(enumClass, captions);
	}

	private static final class EnumCaption {

		private final Map<Enum<?>, String> captions;

		public EnumCaption(Map<Enum<?>, String> captions) {
			this.captions = captions;
		}

		public Enum<?> getEnumByCaption(String caption) throws InvalidEnumCaptionException {
			Optional<Enum<?>> enumItem = captions.entrySet().stream().filter(e -> e.getValue().equals(caption)).findFirst().map(Map.Entry::getKey);

			if (!enumItem.isPresent()) {
				throw new InvalidEnumCaptionException("Unknown enum value " + caption);
			}

			return enumItem.get();
		}
	}

	public static class InvalidEnumCaptionException extends Exception {

		public InvalidEnumCaptionException(String message) {
			super(message);
		}
	}
}
