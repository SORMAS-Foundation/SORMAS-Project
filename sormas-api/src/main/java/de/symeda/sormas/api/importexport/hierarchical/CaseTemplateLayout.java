package de.symeda.sormas.api.importexport.hierarchical;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CaseTemplateLayout {

	public static final String CASE = "case";
	public static final String SAMPLE = "sample";
	public static final String TEST = "test";
	public static final String VACCINATION_ENTITY = "Vaccination";

	private static final Map<String, List<String>> ENTITIES_BY_LEVEL = Map.of(
		CASE,
		List.of(
			"CaseData",
			"Person",
			"Location",
			"HealthConditions",
			"Hospitalization",
			"Symptoms",
			"EpiData",
			"Therapy",
			"MaternalHistory",
			"PortHealthInfo"),
		SAMPLE,
		List.of("Sample"),
		TEST,
		List.of("PathogenTest", "DrugSusceptibility"));

	private CaseTemplateLayout() {
	}

	public static Optional<String> levelOf(String entityName) {
		return ENTITIES_BY_LEVEL.entrySet().stream().filter(entry -> entry.getValue().contains(entityName)).map(Map.Entry::getKey).findFirst();
	}

	public static List<String> entitiesOf(String level) {
		return ENTITIES_BY_LEVEL.getOrDefault(level, List.of());
	}

	public static String join(String entity, String property) {
		return entity.isEmpty() ? property : entity + "." + property;
	}

	public static String entityOf(String header) {
		int dot = header.indexOf('.');
		return dot > 0 ? header.substring(0, dot) : "";
	}

	public static String propertyOf(String header) {
		int dot = header.indexOf('.');
		return dot > 0 ? header.substring(dot + 1) : header;
	}
}
