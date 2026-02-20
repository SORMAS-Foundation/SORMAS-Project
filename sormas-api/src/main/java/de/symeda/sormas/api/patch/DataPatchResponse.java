package de.symeda.sormas.api.patch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

/**
 *
 */
public class DataPatchResponse {

	/**
	 * Actual patched values for the specified keys.
	 * Will NOT contain fields that were NOT patched (even though passed in original patchDictionary).
	 */
	private Map<String, Object> patchDictionary = new HashMap<>();

	/**
	 * Provides the reason for the failure.
	 */
	private Map<String, DataPatchFailure> failures = new HashMap<>();

	public Map<String, Object> getPatchDictionary() {
		return patchDictionary;
	}

	public DataPatchResponse setPatchDictionary(Map<String, Object> patchDictionary) {
		this.patchDictionary = patchDictionary;
		return this;
	}

	public Map<String, DataPatchFailure> getFailures() {
		return failures;
	}

	public DataPatchResponse setFailures(Map<String, DataPatchFailure> failures) {
		this.failures = failures;
		return this;
	}

	/**
	 * True means data was patched on SORMAS entities, false means nothing was changed.
	 * 
	 * @return boolean to indicate if data was patched: operation was a success.
	 */
	public boolean patched() {
		return !failed();
	}

	/**
	 * Patch are atomic operations: either fully or not at all.
	 * 
	 * @return true if operation was NOT applied, else false.
	 */
	public boolean failed() {
		return MapUtils.isNotEmpty(failures);
	}

	@Override
	public String toString() {
		return "DataPatchResponse{" + "patchDictionary=" + patchDictionary + ", failures=" + failures + '}';
	}
}
