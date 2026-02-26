package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class CustomizableEnumPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(CustomizableEnumPatchMapper.class);

	public static final String FALLBACK_NAME = "OTHER";

	// TODO: make configurable.
	private static final List<Language> LANGUAGES = Arrays.asList(Language.EN, Language.FR, Language.DE);

	@EJB
	private CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal customizableEnumFacade;

	@Override
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		String captionCandidate = value.toString();

		if (!CustomizableEnum.class.isAssignableFrom(targetType)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", value, targetType.getName()));
		}

		CustomizableEnumType enumType = CustomizableEnumType.getByEnumClass((Class<? extends CustomizableEnum>) targetType);

		if (enumType == null) {
			throw new IllegalArgumentException(String.format("No CustomizableEnumType could be found for [%s]", targetType.getName()));
		}

		logger.warn("For now only disease-agnostic enum values are retrieved");

		return (T) findCustomizableEnum(captionCandidate, enumType);
	}

	private CustomizableEnum findCustomizableEnum(String captionCandidate, CustomizableEnumType type) {
		String normalizedInput = StringNormalizer.normalize(captionCandidate);

		return searchByDefaultLanguage(type, normalizedInput).or(() -> searchByLanguages(normalizedInput, type))
			.or(
				() -> customizableEnumFacade.getEnumValues(type, null)
					.stream()
					.filter(customizableEnum -> matchByValueOrCaption(customizableEnum, FALLBACK_NAME))
					.findAny())
			.orElseThrow(
				() -> new IllegalStateException(String.format("Could not match value: [%s] to customizableEnumType: [%s]", captionCandidate, type)));
	}

	private Optional<CustomizableEnum> searchByDefaultLanguage(CustomizableEnumType type, String normalizedInput) {
		List<CustomizableEnum> enumValues = customizableEnumFacade.getEnumValues(type, null);

		return enumValues.stream().filter(enumMember -> matchByValueOrCaption(enumMember, normalizedInput)).findFirst();
	}

	public Optional<CustomizableEnum> searchByLanguages(String normalizedInput, CustomizableEnumType type) {
		for (Language language : LANGUAGES) {
			Class<? extends CustomizableEnum> targetType = type.getEnumClass();
			I18nPropertiesRequest request = new I18nPropertiesRequest().setTargetType(targetType)
				.setResourceBundleType(I18nPropertiesRequest.ResourceBundleType.ENUMS)
				.setLanguage(language);
			Map<String, String> resultingMap = I18nProperties.buildKeyValueDictionary(request);

			Optional<CustomizableEnum> customizableEnumOpt = resultingMap.entrySet()
				.stream()
				.filter(entry -> StringNormalizer.normalize(entry.getValue()).equals(normalizedInput))
				.findAny()
				.map(Map.Entry::getKey)
				.map(key -> customizableEnumFacade.getEnumValue(type, null, key));

			if (customizableEnumOpt.isPresent()) {
				return customizableEnumOpt;
			}
		}
		return Optional.empty();
	}

	private static boolean matchByValueOrCaption(CustomizableEnum customizableEnum, String normalizedInput) {
		return StringNormalizer.normalize(customizableEnum.getValue()).equals(normalizedInput)
			|| StringNormalizer.normalize(customizableEnum.getCaption()).equals(normalizedInput);
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return Set.of(CustomizableEnum.class);
	}

	@Override
	public int getOrder() {
		return LOW_PRECEDENCE - (ORDER_CHUNK * 2);
	}
}
