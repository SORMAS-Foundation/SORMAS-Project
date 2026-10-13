package de.symeda.sormas.api.patch.partial_retrieval;

import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In symmetry to the partial patching / partial retrieval is also possible, this represents an attempt to partially retrieve some fields.
 * <p>
 * Implementation is inspired from patching.
 */
@AuditedClass
public class DisplayablePartialRetrievalResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, DisplayableFieldInfo> fieldInfoDictionary = new HashMap<>();

	private Map<String, String> failuresDescriptions = new HashMap<>();

	public Map<String, DisplayableFieldInfo> getFieldInfoDictionary() {
		return fieldInfoDictionary;
	}

	public DisplayablePartialRetrievalResponse setFieldInfoDictionary(Map<String, DisplayableFieldInfo> fieldInfoDictionary) {
		this.fieldInfoDictionary = fieldInfoDictionary;
		return this;
	}

	public Map<String, String> getFailuresDescriptions() {
		return failuresDescriptions;
	}

	public DisplayablePartialRetrievalResponse setFailuresDescriptions(Map<String, String> failuresDescriptions) {
		this.failuresDescriptions = failuresDescriptions;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DisplayablePartialRetrievalResponse that = (DisplayablePartialRetrievalResponse) o;
		return Objects.equals(fieldInfoDictionary, that.fieldInfoDictionary) && Objects.equals(failuresDescriptions, that.failuresDescriptions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldInfoDictionary, failuresDescriptions);
	}

	@Override
	public String toString() {
		return "DisplayablePartialResponse{" + "fieldInfoDictionary=" + fieldInfoDictionary + ", failuresDescriptions=" + failuresDescriptions + '}';
	}
}
