package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE_REFERENCE_NUMBER;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.EPID_NUMBER;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.EXTERNAL_TOKEN;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.PERSON;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Match;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;

final class CaseMatcher {

	static final Map<String, String> ID_COLUMNS = idColumns();

	private final CaseService caseService;
	private final CaseFacadeEjbLocal caseFacade;
	private final PersonFacadeEjbLocal personFacade;

	CaseMatcher(CaseService caseService, CaseFacadeEjbLocal caseFacade, PersonFacadeEjbLocal personFacade) {
		this.caseService = caseService;
		this.caseFacade = caseFacade;
		this.personFacade = personFacade;
	}

	Object bySimilarity(PlannedRecord draft) {
		PersonDto person = draft.target(PERSON);
		CaseDataDto caze = draft.target(CASE);
		List<String> personUuids = personFacade.getSimilarPersonDtos(PersonSimilarityCriteria.forPerson(person))
			.stream()
			.map(SimilarPersonDto::getUuid)
			.collect(Collectors.toList());
		if (personUuids.isEmpty()) {
			return null;
		}
		List<CaseSelectionDto> cases = caseFacade.getSimilarCases(CaseSimilarityCriteria.forCase(caze, personUuids));
		if (cases.size() == 1) {
			return caseService.getByUuid(cases.get(0).getUuid());
		}
		if (cases.size() > 1) {
			return Match.error(null, I18nProperties.getValidationError(Validations.importHierarchicalSimilarCases, cases.size()));
		}
		if (personUuids.size() > 1) {
			return Match.error(null, I18nProperties.getValidationError(Validations.importHierarchicalSimilarPersons, personUuids.size()));
		}
		return personFacade.getByUuid(personUuids.get(0));
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
			List<Case> candidates = sameDisease(draft, column, found.get(column).getOrDefault(value, List.of()));
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

	// SORMAS keeps epid numbers unique per disease, not globally
	private static List<Case> sameDisease(PlannedRecord draft, String column, List<Case> candidates) {
		CaseDataDto caze = draft.target(CASE);
		if (!EPID_NUMBER.equals(column) || caze.getDisease() == null) {
			return candidates;
		}
		return candidates.stream().filter(candidate -> candidate.getDisease() == caze.getDisease()).collect(Collectors.toList());
	}

	private static Map<String, String> idColumns() {
		Map<String, String> columns = new LinkedHashMap<>();
		columns.put(CASE_REFERENCE_NUMBER, Case.CASE_REFERENCE_NUMBER);
		columns.put(EPID_NUMBER, Case.EPID_NUMBER);
		columns.put(EXTERNAL_TOKEN, Case.EXTERNAL_TOKEN);
		return columns;
	}
}
