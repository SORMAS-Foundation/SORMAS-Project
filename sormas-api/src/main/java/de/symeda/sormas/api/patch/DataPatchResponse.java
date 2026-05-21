package de.symeda.sormas.api.patch;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;

/**
 * Response to a patch request.
 */
public class DataPatchResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * True if the dictionary was applied to the specified fields on the given entities.
	 * <p>
	 * Will be false in case of {@link CaseDataPatchRequest#isPatchedInCaseOfFailures()} is false and response contains
	 * {@link DataPatchResponse#failures}
	 */
	private boolean applied = true;

	/**
	 * Actual patched values for the specified keys.
	 * Will NOT contain fields that were NOT patched (even though passed in original patchDictionary).
	 */
	private LinkedHashMap<String, Object> validPatchDictionary = new LinkedHashMap<>();

	/**
	 * Provides the reason for the failure for the impacted fields.
	 */
	private LinkedHashMap<String, DataPatchFailure> failures = new LinkedHashMap<>();

	public LinkedHashMap<String, Object> getValidPatchDictionary() {
		return validPatchDictionary;
	}

	public DataPatchResponse setValidPatchDictionary(LinkedHashMap<String, Object> validPatchDictionary) {
		this.validPatchDictionary = validPatchDictionary;
		return this;
	}

	public LinkedHashMap<String, DataPatchFailure> getFailures() {
		return failures;
	}

	public DataPatchResponse setFailures(LinkedHashMap<String, DataPatchFailure> failures) {
		this.failures = failures;
		return this;
	}

	public boolean isApplied() {
		return applied;
	}

	public DataPatchResponse setApplied(boolean applied) {
		this.applied = applied;
		return this;
	}

	/**
	 * Patch are atomic operations: either fully or not at all.
	 *
	 * @return true if operation was NOT applied, else false.
	 */
	public boolean hasFailures() {
		return MapUtils.isNotEmpty(failures);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DataPatchResponse that = (DataPatchResponse) o;
		return applied == that.applied && Objects.equals(validPatchDictionary, that.validPatchDictionary) && Objects.equals(failures, that.failures);
	}

	@Override
	public int hashCode() {
		return Objects.hash(applied, validPatchDictionary, failures);
	}

	@Override
	public String toString() {
		return "DataPatchResponse{" + "applied=" + applied + ", validPatchDictionary=" + validPatchDictionary + ", failures=" + failures + '}';
	}
}
