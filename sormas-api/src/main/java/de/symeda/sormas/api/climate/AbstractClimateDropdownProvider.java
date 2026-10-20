/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.climate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDataProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * Base for climate settings shown as a single-choice dropdown in system configuration. The stored value is the option
 * value itself; {@link #getOptionCaption(String)} gives the text shown for it.
 */
public abstract class AbstractClimateDropdownProvider implements SystemConfigurationValueDataProvider {

	private static final long serialVersionUID = 1L;

	private static final String KEY = "v1";

	/**
	 * @return the option values in the order they are offered
	 */
	protected abstract List<String> getOptionValues();

	@Override
	public Set<String> getKeys() {
		return Set.of(KEY);
	}

	@Override
	public Map<String, String> getOptions() {

		Map<String, String> options = new LinkedHashMap<>();
		List<String> values = getOptionValues();
		for (int i = 0; i < values.size(); i++) {
			options.put("v" + (i + 1), values.get(i));
		}
		return Collections.unmodifiableMap(options);
	}

	@Override
	public void applyValues(final Map<String, String> values, final SystemConfigurationValueDto dto) {

		if (dto == null || values == null || values.isEmpty()) {
			return;
		}
		values.values().stream().findFirst().ifPresent(dto::setValue);
	}

	@Override
	public Map<String, String> getMappedValues(final SystemConfigurationValueDto dto) {

		if (dto == null || dto.getValue() == null) {
			return Map.of();
		}
		return Map.of(KEY, dto.getValue());
	}
}
