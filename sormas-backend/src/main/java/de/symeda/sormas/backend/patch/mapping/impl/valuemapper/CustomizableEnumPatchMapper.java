package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class CustomizableEnumPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(CustomizableEnumPatchMapper.class);

	public static final String FALLBACK_NAME = "OTHER";

	@EJB
	private CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal customizableEnumFacade;

	@Override
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Object value = request.getValue();
		Class<?> targetType = request.getTargetType();
		String captionCandidate = value.toString();

		if (!CustomizableEnum.class.isAssignableFrom(targetType)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", value, targetType.getName()));
		}

		CustomizableEnumType enumType = CustomizableEnumType.getByEnumClass((Class<? extends CustomizableEnum>) targetType);

		if (enumType == null) {
			throw new IllegalArgumentException(String.format("No CustomizableEnumType could be found for [%s]", targetType.getName()));
		}

		logger.warn("For now only disease-agnostic enum values are retrieved");

		return ValueMappingResult.withData((T) findCustomizableEnum(captionCandidate, enumType, request));
	}

	private CustomizableEnum findCustomizableEnum(String captionCandidate, CustomizableEnumType type, ValuePatchRequest request) {
		String normalizedInput = StringNormalizer.normalize(captionCandidate);

		return searchByDefaultLanguage(type, normalizedInput).or(() -> searchByLanguages(normalizedInput, type, request))
			.or(() -> Optional.ofNullable(customizableEnumFacade.getEnumValue(type, null, FALLBACK_NAME)))
			.orElseThrow(
				() -> new IllegalStateException(String.format("Could not match value: [%s] to customizableEnumType: [%s]", captionCandidate, type)));
	}

	private Optional<CustomizableEnum> searchByDefaultLanguage(CustomizableEnumType type, String normalizedInput) {
		List<CustomizableEnum> enumValues = customizableEnumFacade.getEnumValues(type, null);

		return enumValues.stream().filter(enumMember -> matchByValueOrCaption(enumMember, normalizedInput)).findFirst();
	}

	public Optional<CustomizableEnum> searchByLanguages(String normalizedInput, CustomizableEnumType type, ValuePatchRequest request) {

		List<Language> inputLanguages = request.getInputLanguages();

		if (CollectionUtils.isEmpty(inputLanguages)) {
			inputLanguages = List.of(I18nProperties.getUserLanguage());
		}

		for (Language language : inputLanguages) {
			Class<? extends CustomizableEnum> targetType = type.getEnumClass();
			I18nPropertiesRequest i18nPropertiesRequest = new I18nPropertiesRequest().setTargetType(targetType)
				.setResourceBundleType(I18nPropertiesRequest.ResourceBundleType.ENUMS)
				.setLanguage(language);
			Map<String, String> resultingMap = I18nProperties.buildKeyValueDictionary(i18nPropertiesRequest);

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
