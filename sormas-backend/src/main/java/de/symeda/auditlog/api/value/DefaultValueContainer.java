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
 * Standard-Implementierung zur Formatierung und Speicherung von Attributen.
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
	 *            Zu kopierender {@link ValueContainer}.
	 */
	public DefaultValueContainer(ValueContainer cloneSource) {
		this(DEFAULT_NULL_STRING, new TreeMap<>(cloneSource.getAttributes()));
	}

	/**
	 * @param nullString
	 *            Platzhalter für {@code null}-Values.
	 * @param attributes
	 *            Bereits vorhandene Attribute.
	 */
	protected DefaultValueContainer(String nullString, SortedMap<String, String> attributes) {

		this.attributes = attributes;
		this.nullString = nullString;
		this.anonymizeConfig = new HashMap<>();
	}

	/**
	 * Speichert den zu auditierenden Wert.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
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
	 * Ersetzt geänderte Werte, wenn sie anhand von {@link #getAnonymizeConfig()} zur Anonymisierung vorgesehen sind.
	 * 
	 * @param result
	 */
	private void anonymizeValues(SortedMap<String, String> result) {

		for (Map.Entry<String, String> anonEntry : anonymizeConfig.entrySet()) {
			if (result.containsKey(anonEntry.getKey())) {
				// Geänderten Wert durch anonymisierten String ersetzen
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
	 * Wenn für {@code key} eine Änderung in {@link #compare(ValueContainer)} erkannt wird,
	 * dann wird {@code anonymizeValue} anstatt des neuen Werts zurückgegeben.
	 * 
	 * @param key
	 *            Das zu anonymisierende Attribut.
	 * @param anonymizeValue
	 *            Der anstatt des echten neuen Werts zurückzugebene Wert.
	 */
	public void configureAnonymizeValue(String key, String anonymizeValue) {

		anonymizeConfig.put(key, anonymizeValue);
	}
}
