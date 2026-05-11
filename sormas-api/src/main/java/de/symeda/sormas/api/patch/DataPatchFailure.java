package de.symeda.sormas.api.patch;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Resulting object that is built in case a single field couldn't be mapped during data patching.
 */
public class DataPatchFailure implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private DataPatchFailureCause dataPatchFailureCause;

	@Nullable
	private Object existingFieldValue;

	@Nullable
	private Object providedFieldValue;

	@Nullable
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

	@Override
	public String toString() {
		return "DataPatchFailure{" + "dataPatchFailureCause=" + dataPatchFailureCause + ", existingFieldValue=" + existingFieldValue
			+ ", providedFieldValue=" + providedFieldValue + ", description='" + description + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DataPatchFailure that = (DataPatchFailure) o;
		return dataPatchFailureCause == that.dataPatchFailureCause
			&& Objects.equals(existingFieldValue, that.existingFieldValue)
			&& Objects.equals(providedFieldValue, that.providedFieldValue)
			&& Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataPatchFailureCause, existingFieldValue, providedFieldValue, description);
	}
}
