package de.symeda.sormas.api.patch.mapping;

import java.util.Objects;

import de.symeda.sormas.api.patch.DataPatchFailureCause;

public class ValueMappingResult<T> {

	private T data;
	private DataPatchFailureCause dataPatchFailureCause;

	public static <T> ValueMappingResult<T> withData(T data) {
		ValueMappingResult<T> result = new ValueMappingResult<>();
		result.setData(data);
		return result;
	}

	public static <T> ValueMappingResult<T> withCause(DataPatchFailureCause dataPatchFailureCause) {
		ValueMappingResult<T> result = new ValueMappingResult<>();
		result.setDataPatchFailureCause(dataPatchFailureCause);
		return result;
	}

	public T getData() {
		return data;
	}

	public ValueMappingResult<T> setData(T data) {
		this.data = data;
		return this;
	}

	public DataPatchFailureCause getDataPatchFailureCause() {
		return dataPatchFailureCause;
	}

	public ValueMappingResult<T> setDataPatchFailureCause(DataPatchFailureCause dataPatchFailureCause) {
		this.dataPatchFailureCause = dataPatchFailureCause;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ValueMappingResult<?> that = (ValueMappingResult<?>) o;
		return Objects.equals(data, that.data) && dataPatchFailureCause == that.dataPatchFailureCause;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, dataPatchFailureCause);
	}

	@Override
	public String toString() {
		return "ValueMappingResult{" + "data=" + data + ", dataPatchFailureCause=" + dataPatchFailureCause + '}';
	}
}
