package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.I18nPropertiesRequest;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class ReferenceDtoPatchMapper implements ValuePatchMapper {

	private final static Logger logger = LoggerFactory.getLogger(ReferenceDtoPatchMapper.class);

	@Inject
	private ReferenceDataValueInstanceProvider referenceDataValueInstanceProvider;

	@EJB
	private CountryFacadeEjb.CountryFacadeEjbLocal countryFacade;

	private static final List<Language> LANGUAGES = Arrays.asList(Language.EN, Language.FR, Language.DE);

	@Override
	public <T> ValueMappingResult<T> map(Object value, Class<T> targetType, Set<String> inputLanguageCodes) {
		String captionCandidate = value.toString();

		if (!ReferenceDto.class.isAssignableFrom(targetType)) {
			throw new IllegalArgumentException(String.format("[%s] is not assignable from [%s].", targetType, targetType.getName()));
		}

		Class<? extends ReferenceDto> referenceType = targetType.asSubclass(ReferenceDto.class);

		return this.<T> findByTranslationKey(value, targetType)
			.or(() -> (Optional<? extends T>) findByCaptionMatch(captionCandidate, referenceType))
			.map(ValueMappingResult::withData)
			.orElseGet(() -> {
				logger.info("Could not match value: [{}] to referenceType: [{}]", captionCandidate, referenceType);
				return ValueMappingResult.withCause(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST);
			});
	}

	private <T extends ReferenceDto> Optional<T> findByCaptionMatch(String captionCandidate, Class<T> referenceType) {
		return referenceDataValueInstanceProvider.getOne(captionCandidate, referenceType);
	}

	private <T> Optional<T> findByTranslationKey(Object value, Class<?> referenceType) {

		if (!referenceType.equals(CountryReferenceDto.class)) {
			return Optional.empty();
		}

		String normalizedInput = StringNormalizer.normalize(value.toString());

		for (Language language : LANGUAGES) {
			I18nPropertiesRequest request = new I18nPropertiesRequest().setResourceBundleType(I18nPropertiesRequest.ResourceBundleType.COUNTRY)
				.setTargetType(referenceType)
				.setLanguage(language);
			Map<String, String> stringStringMap = I18nProperties.buildKeyValueDictionary(request);

			Optional<T> enumMemberOpt = stringStringMap.entrySet()
				.stream()
				.filter(entry -> StringNormalizer.normalize(entry.getValue()).equals(normalizedInput))
				.findAny()
				.map(Map.Entry::getKey)
				.map(key -> (T) countryFacade.getCountryByIsoCode(key));

			if (enumMemberOpt.isPresent()) {
				return enumMemberOpt;
			}
		}

		return Optional.empty();
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return Set.of(ReferenceDto.class);
	}

	@Override
	public int getOrder() {
		return LOW_PRECEDENCE - (ORDER_CHUNK * 2);
	}
}
