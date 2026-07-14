/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Resolves an enum constant by name, translating names retired via {@link LegacyEnumNames}.
 */
public final class LegacyEnumHelper {

	/** Cached {@code name -> constant} map per enum class, holding both current and legacy names. */
	private static final Map<Class<?>, Map<String, Enum<?>>> NAMES_BY_TYPE = new ConcurrentHashMap<>();

	private LegacyEnumHelper() {
	}

	/**
	 * @return the constant named {@code name}, or the constant that absorbed it when {@code name} is declared
	 *         in a {@link LegacyEnumNames} annotation.
	 * @throws IllegalArgumentException
	 *             when {@code name} is blank, or is neither a current constant nor a declared legacy name.
	 *             Never returns {@code null}: a blank enum value in untrusted JSON must be rejected, not
	 *             silently accepted.
	 */
	public static <E extends Enum<E>> E resolve(Class<E> enumType, String name) {

		E resolved = resolveOrNull(enumType, name);
		if (resolved == null) {
			throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + StringUtils.trimToEmpty(name));
		}
		return resolved;
	}

	/**
	 * The lenient counterpart of {@link #resolve}, for readers that must tolerate a name from a peer running a
	 * newer version.
	 *
	 * @return {@code null} when {@code name} is blank, or names neither a current constant nor a legacy one.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E resolveOrNull(Class<E> enumType, String name) {

		if (StringUtils.isBlank(name)) {
			return null;
		}
		return (E) names(enumType).get(name.trim());
	}

	/** @return whether {@code enumType} declares at least one legacy name, i.e. whether {@link #resolve} can translate. */
	public static boolean hasLegacyNames(Class<? extends Enum<?>> enumType) {
		return names(enumType).size() > enumType.getEnumConstants().length;
	}

	private static Map<String, Enum<?>> names(Class<? extends Enum<?>> enumType) {

		return NAMES_BY_TYPE.computeIfAbsent(enumType, type -> {
			Map<String, Enum<?>> map = new HashMap<>();
			for (Enum<?> constant : enumType.getEnumConstants()) {
				map.put(constant.name(), constant);
			}

			for (Enum<?> constant : enumType.getEnumConstants()) {
				LegacyEnumNames annotation = annotationOf(enumType, constant);
				if (annotation == null) {
					continue;
				}
				for (String legacyName : annotation.value()) {
					// A legacy name that shadows a live constant, or is claimed twice, would silently resolve to the
					// wrong constant. Both are declaration bugs, so fail rather than guess.
					Enum<?> previous = map.put(legacyName, constant);
					if (previous != null) {
						throw new IllegalStateException(
							"@LegacyEnumNames on " + enumType.getSimpleName() + "." + constant.name() + " declares \"" + legacyName
								+ "\", which is already " + previous.name());
					}
				}
			}
			return Collections.unmodifiableMap(map);
		});
	}

	private static LegacyEnumNames annotationOf(Class<? extends Enum<?>> enumType, Enum<?> constant) {

		try {
			return enumType.getField(constant.name()).getAnnotation(LegacyEnumNames.class);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException("enum constant " + constant.name() + " has no field", e);
		}
	}
}
