package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.jetbrains.annotations.Nullable;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.patch.mapping.ValueMapperDefault;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class EnumPatchMapper implements ValuePatchMapper {

	private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Enum.class);

	private static final String FALLBACK_NAME = "OTHER";

	// TODO: make configurable.
	private static final List<Language> LANGUAGES = Arrays.asList(Language.EN, Language.FR, Language.DE);

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		Class<? extends Enum> enumType = (Class<? extends Enum>) targetType;

		String normalizedInput = StringNormalizer.normalize(value.toString());
		Enum<?>[] constants = enumType.getEnumConstants();

		// default naive search
		T enumMember = searchEnumMemberIgnoringCase(constants, normalizedInput);
		if (enumMember != null) {
			return enumMember;
		}

		for (Language language : LANGUAGES) {
			I18nPropertiesRequest request = new I18nPropertiesRequest().setLanguage(language)
				.setTargetType(enumType)
				.setResourceBundleType(I18nPropertiesRequest.ResourceBundleType.ENUMS);
			Map<String, String> stringStringMap = I18nProperties.buildPropertiesMap(request);

			Optional<T> enumMemberOpt = stringStringMap.entrySet()
				.stream()
				.filter(entry -> StringNormalizer.normalize(entry.getValue()).equals(normalizedInput))
				.findAny()
				.map(Map.Entry::getKey)
				.map(key -> key.replace(I18nProperties.buildPrefix(enumType), ""))
				.map(key -> searchEnumMemberIgnoringCase(constants, key));

			if (enumMemberOpt.isPresent()) {
				return enumMemberOpt.get();
			}
		}

		// overridden fallback
		Enum annotatedDefault = findAnnotatedDefault((Class<? extends Enum<?>>) enumType, constants);
		if (annotatedDefault != null) {
			return (T) annotatedDefault;
		}

		// default fallback
		for (Enum constant : constants) {
			if (FALLBACK_NAME.equals(constant.name())) {
				return (T) constant;
			}
		}

		throw new EnumConstantNotPresentException(enumType, normalizedInput);
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
