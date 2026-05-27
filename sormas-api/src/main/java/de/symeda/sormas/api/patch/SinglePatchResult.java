package de.symeda.sormas.api.patch;

import javax.annotation.Nullable;

import de.symeda.sormas.api.externalmessage.survey.PatchField;

public interface SinglePatchResult {

	PatchField getField();

	@Nullable
	Object getValue();

	@Nullable
	DataPatchFailure getFailure();
}
