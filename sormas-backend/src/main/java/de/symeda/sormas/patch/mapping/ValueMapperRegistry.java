package de.symeda.sormas.patch.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.symeda.sormas.api.patch.mapping.ValueMapper;

@ApplicationScoped
public class ValueMapperRegistry {

	private List<ValueMapper> orderedInstances;

	@Inject
	private Instance<ValueMapper> instances;

	@PostConstruct
	void init() {
		// default sort uses CDI sort.
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	public <T> T map(Object value, Class<T> targetType) {
		for (ValueMapper mapper : orderedInstances) {
			if (mapper.supports(targetType)) {
				return mapper.map(value, targetType);
			}
		}
		throw new IllegalArgumentException(String.format("No mapper found for: [%s]", targetType));
	}
}
