package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TypeToDisplayRegistry {

	private final static Logger logger = LoggerFactory.getLogger(TypeToDisplayRegistry.class);
	public static final String EMPTY_STRING = "";

	private List<TypeToDisplayValueMapper> orderedInstances;

	@Inject
	private Instance<TypeToDisplayValueMapper> instances;

	public TypeToDisplayRegistry() {
	}

	@PostConstruct
	void init() {
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	@NotNull
	public String toDisplayValue(@Nullable Object value) {
		if (value == null) {
			logger.info("Input value was null, using default empty value");
			return EMPTY_STRING;
		}

		Class<?> valueType = value.getClass();
		TypeToDisplayValueMapper matchingMapper = orderedInstances.stream()
			.filter(mapper -> mapper.supports(valueType))
			.findAny()
			.orElseThrow(
				(() -> new IllegalStateException(
					String.format(
						"No mapper found: [%s], Must not occur, default mapper is Object#toString(). Any registered mappers ? [%s]",
						valueType,
						orderedInstances))));

		logger.debug("Value [{}] will be mapped with mapper: [{}]", value, matchingMapper);

		return matchingMapper.toDisplayValue(value);
	}
}
