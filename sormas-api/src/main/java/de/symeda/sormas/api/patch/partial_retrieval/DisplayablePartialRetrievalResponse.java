package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Map;
import java.util.Objects;

public class DisplayablePartialRetrievalResponse {

	private Map<String, DisplayableFieldInfo> fieldInfoDictionary;

	private Map<String, String> failuresDescriptions;

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
