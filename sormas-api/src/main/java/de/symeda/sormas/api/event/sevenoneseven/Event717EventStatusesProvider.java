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
package de.symeda.sormas.api.event.sevenoneseven;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDataProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * System configuration value of the event statuses for which a 7-1-7 assessment can be created, shown as a checkbox per status.
 * The value is the pipe separated list of the {@link EventStatus} names, e.g. {@code CLUSTER|EVENT}.
 * <p>
 * The setting only decides whether a new assessment can be created: the event status at that time is recorded on the assessment,
 * and existing assessments are not affected by later changes of the event status or of this setting. {@link EventStatus#DROPPED}
 * is not offered, because assessments of dropped events are never counted in the 7-1-7 summary.
 */
public class Event717EventStatusesProvider implements SystemConfigurationValueDataProvider {

	private static final long serialVersionUID = 1L;

	public static final String CONFIG_KEY = "EVENT_717_APPLICABLE_EVENT_STATUSES";

	/**
	 * Applies when the setting does not exist or is blank, e.g. in tests.
	 */
	public static final Set<EventStatus> DEFAULT_EVENT_STATUSES = Collections.unmodifiableSet(EnumSet.of(EventStatus.EVENT, EventStatus.CLUSTER));

	/**
	 * The event statuses that can be selected, in the order of the enum.
	 */
	public static final List<EventStatus> SELECTABLE_EVENT_STATUSES =
		Collections.unmodifiableList(Arrays.stream(EventStatus.values()).filter(s -> s != EventStatus.DROPPED).collect(Collectors.toList()));

	@Override
	public Set<String> getKeys() {
		return SELECTABLE_EVENT_STATUSES.stream().map(Enum::name).collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * @return The status names mapped to their captions.
	 */
	@Override
	public Map<String, String> getOptions() {

		Map<String, String> options = new LinkedHashMap<>();
		SELECTABLE_EVENT_STATUSES.forEach(status -> options.put(status.name(), status.toString()));
		return Collections.unmodifiableMap(options);
	}

	@Override
	public void applyValues(final Map<String, String> values, final SystemConfigurationValueDto dto) {

		if (values == null || values.isEmpty()) {
			dto.setValue("");
			return;
		}
		dto.setValue(toValue(values.keySet().stream().filter(getKeys()::contains).map(EventStatus::valueOf).collect(Collectors.toSet())));
	}

	@Override
	public Map<String, String> getMappedValues(final SystemConfigurationValueDto dto) {

		if (dto == null || dto.getValue() == null || dto.getValue().isBlank()) {
			return Collections.emptyMap();
		}

		Set<String> selected = Stream.of(dto.getValue().split("\\|")).map(String::trim).collect(Collectors.toSet());
		return Collections.unmodifiableMap(
			getOptions().entrySet()
				.stream()
				.filter(entry -> selected.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	}

	/**
	 * @return The selectable event statuses contained in the stored value, or {@link #DEFAULT_EVENT_STATUSES} if the value is blank or
	 *         contains none of them.
	 */
	public static Set<EventStatus> parse(String value) {

		if (value == null || value.isBlank()) {
			return DEFAULT_EVENT_STATUSES;
		}

		Set<EventStatus> statuses = EnumSet.noneOf(EventStatus.class);
		for (String name : value.split("\\|")) {
			SELECTABLE_EVENT_STATUSES.stream().filter(status -> status.name().equals(name.trim())).findFirst().ifPresent(statuses::add);
		}
		return statuses.isEmpty() ? DEFAULT_EVENT_STATUSES : Collections.unmodifiableSet(statuses);
	}

	/**
	 * @return The value to store for the given event statuses, in the order of the enum.
	 */
	public static String toValue(Set<EventStatus> statuses) {
		return SELECTABLE_EVENT_STATUSES.stream().filter(statuses::contains).map(Enum::name).collect(Collectors.joining("|"));
	}
}
