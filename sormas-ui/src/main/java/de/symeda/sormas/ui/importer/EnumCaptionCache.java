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

package de.symeda.sormas.ui.importer;

import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

public class EnumCaptionCache {

	private Language language;

	public EnumCaptionCache(Language language) {
		this.language = language;
	}

	private final Map<Class<Enum>, Map<Enum, String>> enumCaptions = new HashMap<>();

	public Enum getEnumByCaption(Class<Enum> enumType, String caption) {
		if (!enumCaptions.containsKey(enumType)) {
			addCaptions(enumCaptions, enumType);
		}

		Map<Enum, String> captions = enumCaptions.get(enumType);

		return captions.entrySet()
			.stream()
			.filter(e -> e.getValue().equalsIgnoreCase(caption))
			.findFirst()
			.map(Map.Entry::getKey)
			.orElseThrow(() -> new IllegalArgumentException("Unknown enum caption " + caption));
	}

	private synchronized void addCaptions(Map<Class<Enum>, Map<Enum, String>> enumCaptions, Class<Enum> enumClass) {

		Map<Enum, String> captions = new HashMap<>();
		for (Enum<?> item : enumClass.getEnumConstants()) {
			captions.put(item, I18nProperties.getEnumCaption(language, item));
		}

		enumCaptions.put(enumClass, captions);
	}
}
