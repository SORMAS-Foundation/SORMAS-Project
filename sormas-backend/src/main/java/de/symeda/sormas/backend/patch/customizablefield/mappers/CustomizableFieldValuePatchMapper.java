package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.Set;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldSetter;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;

/**
 * Contract to specify how a {@link CustomizableFieldType} must be mapped into a value, NOT field specific.
 */
public interface CustomizableFieldValuePatchMapper extends Comparable<CustomizableFieldValuePatchMapper> {

	int HIGH_PRECEDENCE = Integer.MIN_VALUE;

	int LOW_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * Can be used to add it to the default precedences values and a keep some "space between" the implementations ordering.
	 */
	int ORDER_CHUNK = 20;

	/**
	 *
	 * @param request
	 *            to specif how the value should be mapped.
	 * @return actual value
	 * @throws RuntimeException
	 *             in case of the value couldn't be mapped.
	 */
	@NotNull
	ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request);

	@NotNull
	Set<CustomizableFieldType> getSupportedTypes();

	/**
	 * Specifies if the targetType is supported by this class.
	 *
	 * @param targetType
	 *            can be a child class.
	 * @return true if the class will be able to perform some action with this type.
	 */
	default boolean supports(@NotNull CustomizableFieldType targetType) {
		return getSupportedTypes().contains(targetType);
	}

	/**
	 * Allows you to override default mappers.
	 * {@link #HIGH_PRECEDENCE} means this mapper will be used (among) first.
	 * {@link #LOW_PRECEDENCE} means this mapper will be used (among) last.
	 *
	 * @return defaults to LOW_PRECEDENCE
	 */
	default int getOrder() {
		return LOW_PRECEDENCE;
	}

	@Override
	default int compareTo(CustomizableFieldValuePatchMapper o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}

	/**
	 * Creates a new typed instance with the error or the new value to specify.
	 * 
	 * @param initialResult
	 * @param dto
	 * @return
	 */
	static ValueMappingResult<CustomizableFieldValueDto> buildMappingResultFrom(
		@NotNull ValueMappingResult<?> initialResult,
		CustomizableFieldValueDto dto) {

		DataPatchFailureCause dataPatchFailureCause = initialResult.getDataPatchFailureCause();
		if (dataPatchFailureCause != null) {
			return ValueMappingResult.withCause(dataPatchFailureCause);
		}

		return ValueMappingResult.withData(dto);
	}

	static <T> Tuple<Class<T>, CustomizableFieldSetter<T>> buildTuple(Class<T> clazz, CustomizableFieldSetter<T> setter) {
		return Tuple.of(clazz, setter);
	}
}
