package de.symeda.sormas.backend.patch.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EqualityCheckerRegistry {

	private final static Logger logger = LoggerFactory.getLogger(EqualityCheckerRegistry.class);

	private List<EqualityChecker> orderedInstances;

	@Inject
	private Instance<EqualityChecker> instances;

	public EqualityCheckerRegistry() {
	}

	public EqualityCheckerRegistry(Instance<EqualityChecker> instances) {
		this.instances = instances;
	}

	@PostConstruct
	void init() {
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	public boolean areEqual(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}

		Class<?> type = a.getClass();
		EqualityChecker checker = orderedInstances.stream()
			.filter(c -> c.supports(type))
			.findFirst()
			.orElseThrow(
				() -> new IllegalStateException(
					String.format(
						"No equality checker found for: [%s]. Must not occur, ObjectEqualityChecker handles Object. Registered checkers: [%s]",
						type,
						orderedInstances)));

		logger.debug("Values [{}] and [{}] will be compared with checker: [{}]", a, b, checker);
		return checker.areEqual(a, b);
	}
}
