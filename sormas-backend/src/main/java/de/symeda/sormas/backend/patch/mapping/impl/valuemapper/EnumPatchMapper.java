package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMapperDefault;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class EnumPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(EnumPatchMapper.class);

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Enum.class);

	private static final String FALLBACK_NAME = "OTHER";

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Object value = request.getValue();
		Class<?> targetType = request.getTargetType();

		if (!Enum.class.isAssignableFrom(targetType)) {
			return ValueMappingResult.withCause(DataPatchFailureCause.TECHNICAL);
		}

		Class<? extends Enum> enumType = (Class<? extends Enum>) targetType;

		String normalizedInput = StringNormalizer.normalize(value.toString());
		Enum<?>[] constants = enumType.getEnumConstants();

		// default naive search
		T enumMember = searchEnumMemberIgnoringCase(constants, normalizedInput);
		if (enumMember != null) {
			return ValueMappingResult.withData(enumMember);
		}

		List<Language> inputLanguages = request.getInputLanguages();

		if (CollectionUtils.isEmpty(inputLanguages)) {
			inputLanguages = List.of(I18nProperties.getUserLanguage());
		}

		for (Language language : inputLanguages) {
			I18nPropertiesRequest i18nPropertiesRequest = new I18nPropertiesRequest().setLanguage(language)
				.setTargetType(enumType)
				.setResourceBundleType(I18nPropertiesRequest.ResourceBundleType.ENUMS);
			Map<String, String> stringStringMap = I18nProperties.buildKeyValueDictionary(i18nPropertiesRequest);

			Optional<T> enumMemberOpt = stringStringMap.entrySet()
				.stream()
				.filter(entry -> StringNormalizer.normalize(entry.getValue()).equals(normalizedInput))
				.findAny()
				.map(Map.Entry::getKey)
				.map(key -> searchEnumMemberIgnoringCase(constants, key));

			if (enumMemberOpt.isPresent()) {
				return ValueMappingResult.withData(enumMemberOpt.get());
			}
		}

		// overridden fallback
		Enum annotatedDefault = findAnnotatedDefault((Class<? extends Enum<?>>) enumType, constants);
		if (annotatedDefault != null) {
			return ValueMappingResult.withData((T) annotatedDefault);
		}

		// default fallback
		for (Enum constant : constants) {
			if (FALLBACK_NAME.equals(constant.name())) {
				return ValueMappingResult.withData((T) constant);
			}
		}

		logger.info("Could not match value: [{}] to referenceType: [{}]", normalizedInput, targetType);
		return ValueMappingResult.withCause(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST);
	}

	private <T> @Nullable T searchEnumMemberIgnoringCase(Enum<?>[] constants, String normalizedInput) {
		for (Enum<?> constant : constants) {
			if (constant.name().equalsIgnoreCase(normalizedInput)) {
				return (T) constant;
			}
		}
		return null;
	}

	private Enum<?> findAnnotatedDefault(Class<? extends Enum<?>> enumType, Enum<?>[] constants) {
		for (Enum<?> constant : constants) {
			try {
				Field field = enumType.getField(constant.name());
				if (field.isAnnotationPresent(ValueMapperDefault.class)) {
					return constant;
				}
			} catch (NoSuchFieldException e) {
				throw new IllegalStateException(String.format("Cannot occur for enum type [%s] and value: [%s]", enumType, constant), e);
			}
		}

		return null;
	}
}
