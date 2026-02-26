package de.symeda.sormas.backend.patch.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;

@ApplicationScoped
public class ValueMapperRegistry {

	private final static Logger logger = LoggerFactory.getLogger(ValueMapperRegistry.class);
	private List<ValuePatchMapper> orderedInstances;

	@Inject
	private Instance<ValuePatchMapper> instances;

	@PostConstruct
	void init() {
		// default sort uses CDI sort.
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	@NotNull
	public <T> ValueMappingResult<T> map(Object value, @NotNull Class<T> targetType) {
		if (value == null) {
			return null;
		}

		if (targetType.isInstance(value)) {
			return ValueMappingResult.withData(targetType.cast(value));
		}

		for (ValuePatchMapper mapper : orderedInstances) {
			if (mapper.supports(targetType)) {
				return mapper.map(value, targetType);
			}
		}

		logger.error("No mapper found for: [{}]", targetType);

		return ValueMappingResult.withCause(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE);
	}
}
