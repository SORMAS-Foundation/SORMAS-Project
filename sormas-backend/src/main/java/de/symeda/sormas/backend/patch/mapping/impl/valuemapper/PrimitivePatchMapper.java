package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;

@ApplicationScoped
public class PrimitivePatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(PrimitivePatchMapper.class);

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(String.class, Integer.class, Double.class, Float.class, Boolean.class, boolean.class);

	@Override
	@SuppressWarnings("unchecked")
	public <T> ValueMappingResult<T> map(ValuePatchRequest request) {
		Object value = request.getValue();
		Class<?> targetType = request.getTargetType();
		String str = value.toString().trim();

		T result = null;
		try {
			if (targetType == String.class) {
				result = (T) str;
			}
			if (targetType == Integer.class) {
				result = (T) Integer.valueOf(str);
			}
			if (targetType == Double.class) {
				result = (T) Double.valueOf(str);
			}
			if (targetType == Float.class) {
				result = (T) Float.valueOf(str);
			}
			if (targetType == Boolean.class || targetType == boolean.class) {
				result = (T) Boolean.valueOf(str);
			}
		} catch (NumberFormatException e) {
			logger.info("Cannot parse value [{}], expected format: [{}]", value, str, e);
			return ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
		}

		if (result != null) {
			return ValueMappingResult.withData(result);
		}

		throw new IllegalArgumentException("PrimitiveWrapperMapper: unsupported type " + targetType.getName());
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
