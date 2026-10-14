package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE_REFERENCE_NUMBER;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.EPID_NUMBER;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.EXTERNAL_TOKEN;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Match;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;

final class CaseMatcher {

	static final Map<String, String> ID_COLUMNS = idColumns();

	private final CaseService caseService;

	CaseMatcher(CaseService caseService) {
		this.caseService = caseService;
	}

	static boolean hasId(PlannedRecord draft) {
		return ID_COLUMNS.keySet().stream().anyMatch(column -> !draft.node().cell(column).isEmpty());
	}

	static Optional<String> onlyAnId(PlannedRecord record) {
		Map<String, String> cells = record.node().cells();
		if (cells.isEmpty() || !ID_COLUMNS.keySet().containsAll(cells.keySet())) {
			return Optional.empty();
		}
		return cells.keySet().stream().findFirst();
	}

	Map<ImportNode, Object> byId(List<PlannedRecord> drafts) {
		Map<String, Map<String, List<Case>>> found = new HashMap<>();
		ID_COLUMNS.forEach((column, attribute) -> {
			Set<String> values = drafts.stream().map(draft -> draft.node().cell(column)).filter(value -> !value.isEmpty()).collect(Collectors.toSet());
			found.put(
				column,
				caseService.getUndeletedByAttributeValues(attribute, values)
					.stream()
					.collect(Collectors.groupingBy(caze -> String.valueOf(PropertyPaths.read(caze, new String[] { attribute })))));
		});
		Map<ImportNode, Object> result = new IdentityHashMap<>();
		for (PlannedRecord draft : drafts) {
			Object outcome = resolve(draft, found);
			if (outcome != null) {
				result.put(draft.node(), outcome);
			}
		}
		return result;
	}

	private static Object resolve(PlannedRecord draft, Map<String, Map<String, List<Case>>> found) {
		Case chosen = null;
		for (String column : ID_COLUMNS.keySet()) {
			String value = draft.node().cell(column);
			if (value.isEmpty()) {
				continue;
			}
			List<Case> candidates = found.get(column).getOrDefault(value, List.of());
			if (candidates.isEmpty()) {
				continue;
			}
			if (candidates.size() > 1) {
				return Match
					.error(column, I18nProperties.getValidationError(Validations.importHierarchicalCaseIdAmbiguous, value, column, candidates.size()));
			}
			if (chosen != null && !chosen.getUuid().equals(candidates.get(0).getUuid())) {
				return Match.error(null, I18nProperties.getValidationError(Validations.importHierarchicalCaseIdsDisagree));
			}
			chosen = candidates.get(0);
		}
		return chosen;
	}

	private static Map<String, String> idColumns() {
		Map<String, String> columns = new LinkedHashMap<>();
		columns.put(CASE_REFERENCE_NUMBER, Case.CASE_REFERENCE_NUMBER);
		columns.put(EPID_NUMBER, Case.EPID_NUMBER);
		columns.put(EXTERNAL_TOKEN, Case.EXTERNAL_TOKEN);
		return columns;
	}
}
