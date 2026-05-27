package de.symeda.sormas.backend.patch;

import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.DataPatchFailureCause;

/**
 *
 */
// TODO: Extract & Rename
public final class SingleFieldPatchResult {

	private PatchField field;
	private DataPatchFailureCause failureCause;
	private Object value;

	SingleFieldPatchResult(PatchField fieldPath, DataPatchFailureCause cause, Object value) {
		this.field = fieldPath;
		this.failureCause = cause;
		this.value = value;
	}

	public SingleFieldPatchResult() {
	}

	public SingleFieldPatchResult setField(PatchField field) {
		this.field = field;
		return this;
	}

	public SingleFieldPatchResult setFailureCause(DataPatchFailureCause failureCause) {
		this.failureCause = failureCause;
		return this;
	}

	public SingleFieldPatchResult setValue(Object value) {
		this.value = value;
		return this;
	}

	public PatchField getField() {
		return field;
	}

	public DataPatchFailureCause getFailureCause() {
		return failureCause;
	}

	public Object getValue() {
		return value;
	}
}
