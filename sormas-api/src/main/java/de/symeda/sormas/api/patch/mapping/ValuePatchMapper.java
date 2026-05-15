package de.symeda.sormas.api.patch.mapping;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.utils.OrderedRegisterable;

/**
 * Contract to specify how a type must be mapped into a value, NOT field specific.
 */
public interface ValuePatchMapper extends OrderedRegisterable<ValuePatchMapper> {

	/**
	 *
	 * @param value
	 *            raw value type, must either by String or the actual type.
	 * @param targetType
	 *            type that is expected.
	 * @return actual value
	 * @param <T>
	 *            target type
	 * @throws RuntimeException
	 *             in case of the value couldn't be mapped.
	 */
	@NotNull
	default <T> ValueMappingResult<T> map(Object value, @NotNull Class<T> targetType) {
		return this.map(new ValuePatchRequest<T>().setValue(value).setTargetType(targetType));
	}

	/**
	 *
	 * @param request
	 *            to specif how the value should be mapped.
	 * @return actual value
	 * @param <T>
	 *            target type
	 * @throws RuntimeException
	 *             in case of the value couldn't be mapped.
	 */
	@NotNull
	<T> ValueMappingResult<T> map(ValuePatchRequest<T> request);

}
