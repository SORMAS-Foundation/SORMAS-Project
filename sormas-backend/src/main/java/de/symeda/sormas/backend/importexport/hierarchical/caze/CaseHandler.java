package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.PERSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.LevelHandler;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Match;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.user.CurrentUserService;

final class CaseHandler implements LevelHandler {

	private final CaseFacadeEjbLocal caseFacade;
	private final PersonFacadeEjbLocal personFacade;
	private final CaseService caseService;
	private final CurrentUserService currentUserService;
	private final CaseMatcher matcher;
	private final double nameSimilarityThreshold;

	CaseHandler(
		CaseFacadeEjbLocal caseFacade,
		PersonFacadeEjbLocal personFacade,
		CaseService caseService,
		CurrentUserService currentUserService,
		double nameSimilarityThreshold) {
		this.caseFacade = caseFacade;
		this.nameSimilarityThreshold = nameSimilarityThreshold;
		this.personFacade = personFacade;
		this.caseService = caseService;
		this.currentUserService = currentUserService;
		this.matcher = new CaseMatcher(caseService, caseFacade, personFacade);
	}

	@Override
	public Map<String, Object> newTargets(ImportNode node, PlannedRecord parent) {
		PersonDto person = PersonDto.buildImportEntity();
		CaseDataDto caze = CaseDataDto.build(person.toReference(), null);
		caze.setReportingUser(currentUserService.getCurrentUser().toReference());
		Map<String, Object> targets = new HashMap<>();
		targets.put(CASE, caze);
		targets.put(PERSON, person);
		return targets;
	}

	@Override
	public Optional<String> key(PlannedRecord draft) {
		return CaseMatcher.ID_COLUMNS.keySet().stream().map(column -> draft.node().cell(column)).filter(value -> !value.isEmpty()).findFirst();
	}

	@Override
	public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		Map<ImportNode, Case> foundById = new IdentityHashMap<>();
		Map<ImportNode, Case> foundBySimilarity = new IdentityHashMap<>();
		matcher.byId(drafts).forEach((node, outcome) -> sort(node, outcome, matches, foundById));
		drafts.stream()
			.filter(draft -> !CaseMatcher.hasId(draft))
			.forEach(draft -> sort(draft.node(), matcher.bySimilarity(draft), matches, foundBySimilarity));
		matches.putAll(existingMatches(foundById, MatchReason.BY_ID));
		matches.putAll(existingMatches(foundBySimilarity, MatchReason.BY_SIMILARITY));
		linkSamePersonsInFile(drafts, matches);
		return matches;
	}

	private void linkSamePersonsInFile(List<PlannedRecord> drafts, Map<ImportNode, Match> matches) {
		List<PlannedRecord> newPersons = new ArrayList<>();
		drafts.stream().filter(draft -> !matches.containsKey(draft.node())).forEach(draft -> {
			Optional<PlannedRecord> earlier = newPersons.stream().filter(candidate -> samePerson(candidate.target(PERSON), draft.target(PERSON))).findFirst();
			if (earlier.isPresent()) {
				matches.put(draft.node(), caseForPersonOf(earlier.get()));
			} else {
				newPersons.add(draft);
			}
		});
	}

	private Match caseForPersonOf(PlannedRecord earlier) {
		PersonDto person = earlier.target(PERSON);
		CaseDataDto newCase = CaseDataDto.build(person.toReference(), null);
		newCase.setReportingUser(currentUserService.getCurrentUser().toReference());
		Map<String, Object> targets = new HashMap<>();
		targets.put(CASE, newCase);
		targets.put(PERSON, SerializationUtils.clone(person));
		return Match.sharing(earlier.node(), targets, Map.of(PERSON, person), MatchReason.NEW_FOR_PERSON_IN_FILE);
	}

	private boolean samePerson(PersonDto first, PersonDto second) {
		if (StringUtils.isAnyBlank(first.getFirstName(), first.getLastName(), second.getFirstName(), second.getLastName())) {
			return false;
		}
		return PersonHelper
			.areNamesSimilar(first.getFirstName(), first.getLastName(), second.getFirstName(), second.getLastName(), nameSimilarityThreshold)
			&& compatible(first.getSex(), second.getSex())
			&& compatible(first.getBirthdateDD(), second.getBirthdateDD())
			&& compatible(first.getBirthdateMM(), second.getBirthdateMM())
			&& compatible(first.getBirthdateYYYY(), second.getBirthdateYYYY());
	}

	private static boolean compatible(Object first, Object second) {
		return first == null || second == null || first.equals(second);
	}

	private void sort(ImportNode node, Object outcome, Map<ImportNode, Match> matches, Map<ImportNode, Case> found) {
		if (outcome instanceof Match) {
			matches.put(node, (Match) outcome);
		} else if (outcome instanceof Case) {
			found.put(node, (Case) outcome);
		} else if (outcome instanceof PersonDto) {
			matches.put(node, newCaseFor((PersonDto) outcome));
		}
	}

	private Match newCaseFor(PersonDto existingPerson) {
		CaseDataDto newCase = CaseDataDto.build(existingPerson.toReference(), null);
		newCase.setReportingUser(currentUserService.getCurrentUser().toReference());
		Map<String, Object> targets = new HashMap<>();
		targets.put(CASE, newCase);
		targets.put(PERSON, existingPerson);
		return Match.createWith(
			existingPerson.getUuid(),
			targets,
			Map.of(PERSON, existingPerson),
			List.of(existingPerson.getChangeDate()),
			MatchReason.NEW_FOR_EXISTING_PERSON);
	}

	private Map<ImportNode, Match> existingMatches(Map<ImportNode, Case> found, MatchReason reason) {
		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		Map<ImportNode, Case> editable = new IdentityHashMap<>();
		Map<Case, EditPermissionType> permissions = caseService.getEditPermissionTypes(found.values().stream().distinct().collect(Collectors.toList()));
		found.forEach((node, caze) -> {
			if (permissions.get(caze) == EditPermissionType.ALLOWED) {
				editable.put(node, caze);
			} else {
				matches.put(node, Match.error(null, I18nProperties.getValidationError(Validations.importHierarchicalCaseNotEditable, caze.getUuid())));
			}
		});
		if (editable.isEmpty()) {
			return matches;
		}
		Map<String, CaseDataDto> cases = caseFacade.getByUuids(editable.values().stream().map(Case::getUuid).distinct().collect(Collectors.toList()))
			.stream()
			.collect(Collectors.toMap(CaseDataDto::getUuid, Function.identity()));
		Map<String, PersonDto> persons = personFacade
			.getByUuids(cases.values().stream().map(caze -> caze.getPerson().getUuid()).distinct().collect(Collectors.toList()))
			.stream()
			.collect(Collectors.toMap(PersonDto::getUuid, Function.identity()));
		editable.forEach((node, entity) -> {
			CaseDataDto caze = cases.get(entity.getUuid());
			PersonDto person = persons.get(caze.getPerson().getUuid());
			Map<String, Object> targets = new HashMap<>();
			targets.put(CASE, caze);
			targets.put(PERSON, person);
			matches.put(node, Match.existing(caze.getUuid(), targets, Arrays.asList(caze.getChangeDate(), person.getChangeDate()), reason));
		});
		return matches;
	}

	@Override
	public String label(PlannedRecord record) {
		PersonDto person = record.target(PERSON);
		CaseDataDto caze = record.target(CASE);
		String name = (Objects.toString(person.getFirstName(), "") + " " + Objects.toString(person.getLastName(), "")).strip();
		String storedId = record.isNew() ? "" : DataHelper.getShortUuid(caze.getUuid());
		String id = Stream.of(caze.getEpidNumber(), caze.getExternalToken()).filter(StringUtils::isNotBlank).findFirst().orElse(storedId);
		if (name.isEmpty() || id.isEmpty()) {
			return name + id;
		}
		return name + " (" + id + ")";
	}

	@Override
	public void complete(PlannedRecord record) {
		Optional<String> idColumn = record.isNew() ? CaseMatcher.onlyAnId(record) : Optional.empty();
		if (idColumn.isPresent()) {
			String value = record.node().cell(idColumn.get());
			record.reportIssue(idColumn.get(), I18nProperties.getValidationError(Validations.importHierarchicalCaseIdNotFound, value, idColumn.get()));
			return;
		}
		if (record.isNew()) {
			Stream.of(UserRight.CASE_IMPORT, UserRight.CASE_CREATE)
				.filter(right -> !currentUserService.hasUserRight(right))
				.forEach(right -> record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, right)));
		}
		if (record.reason() == MatchReason.NEW_FOR_EXISTING_PERSON && !currentUserService.hasUserRight(UserRight.PERSON_EDIT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.PERSON_EDIT));
		}
		if (record.operation() == OperationType.UPDATE && !currentUserService.hasUserRight(UserRight.CASE_EDIT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_EDIT));
		}
		if (record.operation() == OperationType.UNCHANGED) {
			return;
		}
		if (record.reason() == MatchReason.NEW_FOR_PERSON_IN_FILE && !record.changes().isEmpty()) {
			record.reportIssue(
				null,
				I18nProperties.getValidationError(Validations.importHierarchicalPersonDiffersInFile, record.linkedRoot().lineNumber()));
			return;
		}
		if (MaskedRecords.isMasked(record)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMaskedRecord));
			return;
		}
		if (RejectedRecords.alreadyRejected(record)) {
			return;
		}
		CaseDataDto caze = record.target(CASE);
		if (caze.getEpidNumber() != null && caseFacade.doesEpidNumberExist(caze.getEpidNumber(), caze.getUuid(), caze.getDisease())) {
			record.reportIssue(CaseTemplateSchema.EPID_NUMBER, I18nProperties.getString(Strings.messageEpidNumberWarning));
		}
		try {
			personFacade.validate(record.target(PERSON));
		} catch (ValidationRuntimeException e) {
			ValidationIssues.report(record.node(), PERSON, e);
		}
		try {
			caseFacade.validate(record.target(CASE));
		} catch (ValidationRuntimeException e) {
			ValidationIssues.report(record.node(), CASE, e);
		}
	}
}
