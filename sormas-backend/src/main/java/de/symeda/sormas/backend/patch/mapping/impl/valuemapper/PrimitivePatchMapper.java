package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class PrimitivePatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(PrimitivePatchMapper.class);

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
		String.class,
		int.class,
		Integer.class,
		long.class,
		Long.class,
		BigDecimal.class,
		double.class,
		Double.class,
		float.class,
		Float.class,
		Boolean.class,
		boolean.class);

	private final Map<Class<?>, Function<String, ?>> NUMBER_TYPES_DICTIONARY = Map.of(
		String.class,
		Function.identity(),

		Integer.class,
		Integer::valueOf,

		int.class,
		Integer::parseInt,

		Long.class,
		Long::valueOf,

		long.class,
		Long::parseLong,

		BigDecimal.class,
		BigDecimal::new,

		double.class,
		Double::parseDouble,

		Double.class,
		Double::valueOf,

		float.class,
		Float::parseFloat,

		Float.class,
		Float::valueOf);

	public static final String YES_I18N_KEY = "yes";

	@Override
	@SuppressWarnings("unchecked")
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Object value = request.getValue();
		Class<?> targetType = request.getTargetType();
		String str = value.toString().trim();

		T result = null;

		if (targetType.equals(String.class)) {
			result = (T) str;
		}

		Function<String, ?> numberTypeFct = NUMBER_TYPES_DICTIONARY.get(targetType);
		if (numberTypeFct != null) {
			try {
				result = (T) numberTypeFct.apply(str);
			} catch (NumberFormatException e) {
				logger.info("Cannot parse value [{}], expected format: [{}]", value, str, e);
				return ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
			}
		}

		if (targetType == Boolean.class || targetType == boolean.class) {
			result = parseBoolean(request, str);
		}

		if (result != null) {
			return ValueMappingResult.withData(result);
		}

		throw new IllegalArgumentException("PrimitiveWrapperMapper: unsupported type " + targetType.getName());
	}

	private static <T> @NotNull T parseBoolean(ValuePatchRequest<T> request, String str) {
		T result;
		List<Language> inputLanguages = request.getInputLanguages();

		if (CollectionUtils.isEmpty(inputLanguages)) {
			inputLanguages = List.of(I18nProperties.getUserLanguage());
		}

		String normalizedStr = StringNormalizer.normalize(str);

		result = (T) inputLanguages.stream()
			.map(language -> I18nProperties.getString(language, YES_I18N_KEY))
			.filter(translation -> StringNormalizer.normalize(translation).equalsIgnoreCase(normalizedStr))
			.findAny()
			.map(ignored -> true)
			.orElseGet(() -> Boolean.valueOf(str));
		return result;
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
