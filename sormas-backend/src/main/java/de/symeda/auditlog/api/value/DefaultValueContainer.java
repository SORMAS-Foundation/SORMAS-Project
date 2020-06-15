/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api.value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import de.symeda.auditlog.api.value.format.EnumFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Standard implementation to format and save attributes.
 * 
 * @author Oliver Milke, Stefan Kock
 */
public class DefaultValueContainer implements SimpleValueContainer {

	private static final long serialVersionUID = 8395517561701644765L;

	private final EnumFormatter enumFormatter = new EnumFormatter();

	private final SortedMap<String, String> attributes;
	private final String nullString;
	private final Map<String, String> anonymizeConfig;

	/**
	 * <code>nullString = {@value ValueContainer#DEFAULT_NULL_STRING}</code>
	 */
	public DefaultValueContainer() {
		this(DEFAULT_NULL_STRING);
	}

	/**
	 * @param nullString
	 */
	public DefaultValueContainer(String nullString) {

		this(nullString, new TreeMap<>());
	}

	/**
	 * @param cloneSource
	 *            {@link ValueContainer} to copy.
	 */
	public DefaultValueContainer(ValueContainer cloneSource) {
		this(DEFAULT_NULL_STRING, new TreeMap<>(cloneSource.getAttributes()));
	}

	/**
	 * @param nullString
	 *            Placeholder for {@code null} values.
	 * @param attributes
	 *            Already existing attributes.
	 */
	protected DefaultValueContainer(String nullString, SortedMap<String, String> attributes) {

		this.attributes = attributes;
		this.nullString = nullString;
		this.anonymizeConfig = new HashMap<>();
	}

	/**
	 * Saves the value to be audited.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} is saved as the value.
	 */
	protected void putToMap(String key, Object value) {

		attributes.put(key, Objects.toString(value, getNullString()));
	}

	@Override
	public SortedMap<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public String getNullString() {
		return nullString;
	}

	public Map<String, String> getAnonymizeConfig() {
		return anonymizeConfig;
	}

	@Override
	public SortedMap<String, String> compare(ValueContainer originalState) {

		SortedMap<String, String> result = new TreeMap<>(getAttributes());
		result.entrySet().removeAll(originalState.getAttributes().entrySet());

		anonymizeValues(result);

		return result;
	}

	@Override
	public SortedMap<String, String> getChanges() {

		TreeMap<String, String> map = new TreeMap<>(getAttributes());
		anonymizeValues(map);
		return map;
	}

	/**
	 * Replaces changed values when they are designated for anonymization by {@link #getAnonymizeConfig()}.
	 * 
	 * @param result
	 */
	private void anonymizeValues(SortedMap<String, String> result) {

		for (Map.Entry<String, String> anonEntry : anonymizeConfig.entrySet()) {
			if (result.containsKey(anonEntry.getKey())) {
				// Replace the changed value with an anonymized String
				result.put(anonEntry.getKey(), anonEntry.getValue());
			}
		}
	}

	@Override
	public void put(String key, String value) {

		putToMap(key, value);
	}

	@Override
	public <V> void put(String key, V value, ValueFormatter<V> valueFormatter) {

		putToMap(key, value == null ? null : valueFormatter.format(value));
	}

	@Override
	public void put(String key, Boolean value) {

		putToMap(key, value);
	}

	@Override
	public void put(String key, Number value) {

		putToMap(key, value);
	}

	@Override
	public void put(String key, Enum<?> value) {

		put(key, value, enumFormatter);
	}

	@Override
	public void put(String key, Date date, String datePattern) {

		put(key, date, value -> new SimpleDateFormat(datePattern).format(value));
	}

	/**
	 * When a change is detected for {@code key} in {@link #compare(ValueContainer)},
	 * {@code anonymizeValue} will be returned instead of the new value.
	 * 
	 * @param key
	 *            The attribute to anonymize.
	 * @param anonymizeValue
	 *            The value that is returned instead of the actual new value.
	 */
	public void configureAnonymizeValue(String key, String anonymizeValue) {

		anonymizeConfig.put(key, anonymizeValue);
	}
}
