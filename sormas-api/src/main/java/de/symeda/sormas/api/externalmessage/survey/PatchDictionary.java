package de.symeda.sormas.api.externalmessage.survey;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Wrapper around concrete storage for patch dictionary.
 */
@AuditedClass
public class PatchDictionary implements Serializable {

	@NotNull
	private LinkedHashMap<PatchField, Object> dictionary = new LinkedHashMap<>();

	/**
	 * Helper method when you don't have any grouped fields.
	 * 
	 * @param key
	 *            {@link PatchField#getField()}
	 * @param value
	 *            value within dictionary
	 */
	public void put(String key, Object value) {
		dictionary.put(new PatchField().setField(key), value);
	}

	public void put(PatchField key, Object value) {
		dictionary.put(key, value);
	}

	public LinkedHashMap<PatchField, Object> getDictionary() {
		return dictionary;
	}

	public PatchDictionary setDictionary(LinkedHashMap<PatchField, Object> dictionary) {
		this.dictionary = dictionary;
		return this;
	}

	@JsonIgnore
	public PatchDictionary setNonTypedPatchDictionary(Map<PatchField, Object> patchDictionary) {
		this.dictionary = new LinkedHashMap<>(patchDictionary);
		return this;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return dictionary.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PatchDictionary that = (PatchDictionary) o;
		return Objects.equals(dictionary, that.dictionary);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dictionary);
	}

	@Override
	public String toString() {
		return "PatchDictionary{" + "dictionary=" + dictionary + '}';
	}
}
