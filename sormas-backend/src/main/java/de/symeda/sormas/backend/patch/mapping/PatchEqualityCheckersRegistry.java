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
public class PatchEqualityCheckersRegistry {

	private final static Logger logger = LoggerFactory.getLogger(PatchEqualityCheckersRegistry.class);

	private List<PatchingEqualityChecker> orderedInstances;

	@Inject
	private Instance<PatchingEqualityChecker> instances;

	public PatchEqualityCheckersRegistry() {
	}

	public PatchEqualityCheckersRegistry(Instance<PatchingEqualityChecker> instances) {
		this.instances = instances;
	}

	@PostConstruct
	void init() {
		orderedInstances = instances.stream().sorted().collect(Collectors.toList());
	}

	public boolean areEqual(Object a, Object b) {
		if (a == null && b == null) {
			logger.debug("Both values were null, returning true");
			return true;
		}
		if (a == null || b == null) {
			logger.debug("One of both value was null, returning false.");
			return false;
		}

		Class<?> type = a.getClass();
		PatchingEqualityChecker checker = orderedInstances.stream()
			.filter(c -> c.supports(type))
			.findFirst()
			.orElseThrow(
				() -> new IllegalStateException(
					String.format(
						"No equality checker found for: [%s]. Must not occur, ObjectEqualityChecker handles Object. Registered checkers: [%s]",
						type,
						orderedInstances)));

		logger.debug("Values [{}] and [{}] will be compared with checker: [{}]", a, b, checker);
		boolean areEqual = checker.areEqual(a, b);

		logger.debug("areEqual: [{}]", areEqual);

		return areEqual;
	}
}
