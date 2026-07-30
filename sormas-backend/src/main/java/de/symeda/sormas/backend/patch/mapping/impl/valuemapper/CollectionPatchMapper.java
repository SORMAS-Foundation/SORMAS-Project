package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;
import de.symeda.sormas.backend.util.InstanceProvider;

/**
 * Edge-case handling: field which is a collection of a singular values: Set/List.
 * Calls the registry (where it's also contained!) to allow to map to every supported type.
 */
@ApplicationScoped
public class CollectionPatchMapper implements ValuePatchMapper {

	private static final Logger logger = LoggerFactory.getLogger(CollectionPatchMapper.class);

	public static final String SEPARATOR = ",";

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Set.class, List.class);

	public static final Map<Class<?>, Collector<?, ?, ?>> COLLECTOR_DICTIONARY =
		Map.of(Set.class, Collectors.toSet(), List.class, Collectors.toList());

	/**
	 * Not injected through CDI, but lazily to avoid cycle: Registry <-> CollectionPatchMapper
	 */
	private ValueMapperRegistry valueMapperRegistry;

	@Override
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Class<T> collectionSubType = request.getCollectionSubType();
		if (collectionSubType == null) {
			logger.warn("CollectionSubType is null for request: [{}]", request);
			return ValueMappingResult.withCause(DataPatchFailureCause.TECHNICAL);
		}

		Object groupedValue = request.getValue();

		if (groupedValue.getClass() != String.class) {
			logger.warn(
				"CollectionSubType: [{}] only supports matching type or string, split with ',' (commas). Request: [{}]",
				collectionSubType,
				request);
			return ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
		}

		List<ValueMappingResult<?>> mappingResults = Arrays.stream(((String) groupedValue).split(SEPARATOR))
			.filter(StringUtils::isNotBlank)
			.map(String::trim)
			.map(singleValue -> getValueMapperRegistry().map(buildRequestFrom(request, singleValue)))
			.collect(Collectors.toList());

		if (mappingResults.isEmpty()) {
			logger.warn("The input string was empty, therefore no value can be patched within the collection, this seems off: [{}]", request);
			return ValueMappingResult.withData(null);
		}

		Optional<ValueMappingResult<?>> failureOpt = mappingResults.stream().filter(result -> result.getDataPatchFailureCause() != null).findAny();
		Collector appropriateCollector = COLLECTOR_DICTIONARY.get(request.getTargetType());

		return failureOpt.<ValueMappingResult<T>> map(result -> ValueMappingResult.withCause(result.getDataPatchFailureCause()))
			.orElseGet(() -> ValueMappingResult.withData((T) mappingResults.stream().map(ValueMappingResult::getData).collect(appropriateCollector)));

	}

	private static ValuePatchRequest<?> buildRequestFrom(ValuePatchRequest<?> request, String singleValue) {
		return new ValuePatchRequest().setInputLanguages(request.getInputLanguages())
			.setValue(singleValue)
			.setAllowFallbackValues(request.isAllowFallbackValues())
			.setTargetType(request.getCollectionSubType());
	}

	private ValueMapperRegistry getValueMapperRegistry() {
		if (valueMapperRegistry == null) {
			valueMapperRegistry = InstanceProvider.getInstanceFor(ValueMapperRegistry.class);
		}

		return valueMapperRegistry;
	}

	public void setValueMapperRegistry(ValueMapperRegistry valueMapperRegistry) {
		this.valueMapperRegistry = valueMapperRegistry;
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
