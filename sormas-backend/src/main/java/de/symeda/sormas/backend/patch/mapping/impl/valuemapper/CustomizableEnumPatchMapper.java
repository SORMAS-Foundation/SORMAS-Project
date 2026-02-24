package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;

@ApplicationScoped
public class CustomizableEnumPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(CustomizableEnumPatchMapper.class);

	public static final String FALLBACK_NAME = "OTHER";

	@EJB
	private CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal customizableEnumFacade;

	@Override
	public <T> T map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		String captionCandidate = value.toString();

		if (!targetType.isAssignableFrom(CustomizableEnum.class)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", value, targetType.getName()));
		}

		CustomizableEnumType enumType = CustomizableEnumType.getByEnumClass((Class<? extends CustomizableEnum>) targetType);

		if (enumType == null) {
			throw new IllegalArgumentException(String.format("No CustomizableEnumType could be found for [%s]", targetType.getName()));
		}

		Class<? extends ReferenceDto> referenceType = targetType.asSubclass(ReferenceDto.class);

		logger.warn("For now only disease-agnostic enum values are retrieved");
		List<CustomizableEnum> enumValues = customizableEnumFacade.getEnumValues(enumType, null);

		return (T) findCustomizableEnum(enumValues, captionCandidate, referenceType);
	}

	private static CustomizableEnum findCustomizableEnum(
		List<CustomizableEnum> enumValues,
		String captionCandidate,
		Class<? extends ReferenceDto> referenceType) {
		String normalizedCandidate = captionCandidate.trim().replace(" ", "_").toUpperCase();

		// TODO: check if we want to check also i18n translations or captions.
		return enumValues.stream()
			.filter(customizableEnum -> customizableEnum.getValue().equals(normalizedCandidate))
			.findAny()
			.or(() -> enumValues.stream().filter(customizableEnum -> customizableEnum.getValue().equals(FALLBACK_NAME)).findAny())
			.orElseThrow(
				() -> new IllegalStateException(
					String.format("Could not match value: [%s] to referenceType: [%s]", captionCandidate, referenceType)));
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
