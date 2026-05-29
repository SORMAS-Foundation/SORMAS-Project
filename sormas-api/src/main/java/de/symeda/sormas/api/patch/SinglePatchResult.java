package de.symeda.sormas.api.patch;

import javax.annotation.Nullable;

import de.symeda.sormas.api.externalmessage.survey.PatchField;

/**
 * Represents an attempt to a patch a single field.
 * Either {@link #getValue()} or {@link #getFailure()} will be present, but not both at same time.
 */
public interface SinglePatchResult {

	PatchField getField();

	/**
	 * Will be null if {@link #getFailure()} is present
	 * 
	 * @return value that will be patched.
	 */
	@Nullable
	Object getValue();

	/**
	 * Will be null if {@link #getValue()} ()} is present.
	 * 
	 * @return failure reason when attempt to patch field.
	 */
	@Nullable
	DataPatchFailure getFailure();
}
