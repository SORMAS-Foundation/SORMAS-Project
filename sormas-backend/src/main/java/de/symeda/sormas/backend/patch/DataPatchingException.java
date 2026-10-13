package de.symeda.sormas.backend.patch;

import de.symeda.sormas.api.patch.DataPatchFailureCause;

/**
 * In some scenarios, it's simpler to throw a specific exception for exceptional exceptions that should not happen too often.
 */
public class DataPatchingException extends RuntimeException {

	private final DataPatchFailureCause failureCause;

	public DataPatchingException(String message, DataPatchFailureCause failureCause) {
		super(message);
		this.failureCause = failureCause;
	}

	public DataPatchFailureCause getFailureCause() {
		return failureCause;
	}
}
