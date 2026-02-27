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
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;

@ApplicationScoped
public class ValueMapperRegistry {

	private final static Logger logger = LoggerFactory.getLogger(ValueMapperRegistry.class);
	private List<ValuePatchMapper> orderedInstances;

	@Inject
	private Instance<ValuePatchMapper> instances;

	public ValueMapperRegistry() {
	}

	public ValueMapperRegistry(Instance<ValuePatchMapper> instances) {
		this.instances = instances;
	}

	@PostConstruct
	void init() {
		// default sort uses CDI sort.
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	@NotNull
	public <T> ValueMappingResult<T> map(ValuePatchRequest<T> request) {
		Class<?> targetType = request.getTargetType();

		if (targetType == Object.class) {
			logger.error("Object is not a supported targetType");
			return ValueMappingResult.withCause(DataPatchFailureCause.TECHNICAL);
		}

		Object value = request.getValue();
		if (value == null) {
			return ValueMappingResult.withData(null);
		}

		if (targetType.isInstance(value)) {
			return ValueMappingResult.withData((T) targetType.cast(value));
		}

		for (ValuePatchMapper mapper : orderedInstances) {
			if (mapper.supports(targetType)) {
				return mapper.map(request);
			}
		}

		logger.error("No mapper found for: [{}]", targetType);

		return ValueMappingResult.withCause(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE);
	}
}
