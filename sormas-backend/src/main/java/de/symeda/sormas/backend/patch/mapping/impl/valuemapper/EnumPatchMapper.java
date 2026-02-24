package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.patch.mapping.ValueMapperDefault;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;

@ApplicationScoped
public class EnumPatchMapper implements ValuePatchMapper {

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
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		// TODO: check if inputLanguageCodes can be used to use I18N.
		Class<? extends Enum> enumType = (Class<? extends Enum>) targetType;

		String memberNameCandidate = value.toString().trim().toUpperCase();
		Enum<?>[] constants = enumType.getEnumConstants();

		for (Enum constant : constants) {
			if (constant.name().equalsIgnoreCase(memberNameCandidate)) {
				return (T) constant;
			}
		}

		// TODO: check if fallback mechanism is wanted.
		for (Enum constant : constants) {
			if (FALLBACK_NAME.equals(constant.name())) {
				return (T) constant;
			}
		}

		Enum annotatedDefault = findAnnotatedDefault((Class<? extends Enum<?>>) enumType, constants);
		if (annotatedDefault != null) {
			return (T) annotatedDefault;
		}

		throw new EnumConstantNotPresentException(enumType, memberNameCandidate);
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
