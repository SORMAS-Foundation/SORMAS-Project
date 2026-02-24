package de.symeda.sormas.backend.patch.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;

@ApplicationScoped
public class ValueMapperRegistry {

	private List<ValuePatchMapper> orderedInstances;

	@Inject
	private Instance<ValuePatchMapper> instances;

	@PostConstruct
	void init() {
		// default sort uses CDI sort.
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	@NotNull
	public <T> T map(Object value, @NotNull Class<T> targetType) {
		if (value == null) {
			return null;
		}

		if (targetType.isInstance(value)) {
			return targetType.cast(value);
		}

		for (ValuePatchMapper mapper : orderedInstances) {
			if (mapper.supports(targetType)) {
				return mapper.map(value, targetType);
			}
		}
		throw new IllegalArgumentException(String.format("No mapper found for: [%s]", targetType));
	}
}
