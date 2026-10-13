package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Map;
import java.util.Objects;

public class PartialRetrievalResponse {

	private Map<String, FieldInfo> fieldInfoDictionary;

	private Map<String, PartialRetrievalFailureCause> failuresDictionary;

	public Map<String, FieldInfo> getFieldInfoDictionary() {
		return fieldInfoDictionary;
	}

	public PartialRetrievalResponse setFieldInfoDictionary(Map<String, FieldInfo> fieldInfoDictionary) {
		this.fieldInfoDictionary = fieldInfoDictionary;
		return this;
	}

	public Map<String, PartialRetrievalFailureCause> getFailuresDictionary() {
		return failuresDictionary;
	}

	public PartialRetrievalResponse setFailuresDictionary(Map<String, PartialRetrievalFailureCause> failuresDictionary) {
		this.failuresDictionary = failuresDictionary;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PartialRetrievalResponse that = (PartialRetrievalResponse) o;
		return Objects.equals(fieldInfoDictionary, that.fieldInfoDictionary) && Objects.equals(failuresDictionary, that.failuresDictionary);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldInfoDictionary, failuresDictionary);
	}

	@Override
	public String toString() {
		return "PartialRetrievalResponse{" + "fieldInfoDictionary=" + fieldInfoDictionary + ", failuresDictionary=" + failuresDictionary + '}';
	}
}
