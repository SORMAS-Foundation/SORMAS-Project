package de.symeda.sormas.backend.patch.mapping;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.utils.Tuple;

@ApplicationScoped
public class FieldCustomMapperRegistry {

	@Inject
	private Instance<FieldCustomMapper> instances;

	private Map<String, FieldCustomMapper> dictionary;

	@PostConstruct
	void init() {
		dictionary = instances.stream()
			.flatMap(mapperInstance -> mapperInstance.supportedFields().stream().map(field -> new Tuple<>(field, mapperInstance)))
			.collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
	}

	public Optional<FieldCustomMapper> getMapper(final String fieldName, Disease disease) {
		return Optional.ofNullable(dictionary.get(fieldName)).filter(mapper -> mapper.supportedDisease().contains(disease));
	}
}
