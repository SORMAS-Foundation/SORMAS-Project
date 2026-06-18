package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;

@ApplicationScoped
public class CustomizableFieldValuePatchMapperRegistry {

	private static final Logger logger = LoggerFactory.getLogger(CustomizableFieldValuePatchMapperRegistry.class);

	private List<CustomizableFieldValuePatchMapper> orderedInstances;

	@Inject
	private Instance<CustomizableFieldValuePatchMapper> instances;

	public CustomizableFieldValuePatchMapperRegistry() {
	}

	public CustomizableFieldValuePatchMapperRegistry(Instance<CustomizableFieldValuePatchMapper> instances) {
		this.instances = instances;
	}

	@PostConstruct
	void init() {
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	public ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request) {
		CustomizableFieldType targetType = request.getTargetType();

		for (CustomizableFieldValuePatchMapper mapper : orderedInstances) {
			if (mapper.supports(targetType)) {
				return mapper.map(request);
			}
		}

		logger.error("No CustomizableFieldValuePatchMapper mapper found for: [{}]", targetType);
		return ValueMappingResult.withCause(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE);
	}

	Set<CustomizableFieldType> getAllSupportedTypes() {
		return orderedInstances.stream().flatMap(mapper -> mapper.getSupportedTypes().stream()).collect(Collectors.toSet());
	}
}
