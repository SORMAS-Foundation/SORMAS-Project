package de.symeda.sormas.api.patch;

public class DataPatchFailure {

	private DataPatchFailureCause dataPatchFailureCause;

	private Object existingFieldValue;
	private Object providedFieldValue;

	private String description;

	public DataPatchFailureCause getDataPatchFailureCause() {
		return dataPatchFailureCause;
	}

	public DataPatchFailure setDataPatchFailureCause(DataPatchFailureCause dataPatchFailureCause) {
		this.dataPatchFailureCause = dataPatchFailureCause;
		return this;
	}

	public Object getExistingFieldValue() {
		return existingFieldValue;
	}

	public DataPatchFailure setExistingFieldValue(Object existingFieldValue) {
		this.existingFieldValue = existingFieldValue;
		return this;
	}

	public Object getProvidedFieldValue() {
		return providedFieldValue;
	}

	public DataPatchFailure setProvidedFieldValue(Object providedFieldValue) {
		this.providedFieldValue = providedFieldValue;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public DataPatchFailure setDescription(String description) {
		this.description = description;
		return this;
	}
}
