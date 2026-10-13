package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.CASE_EXTERNAL_ID;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.CASE_UUID;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.PERSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.person.PersonDto;
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

	CaseHandler(CaseFacadeEjbLocal caseFacade, PersonFacadeEjbLocal personFacade, CaseService caseService, CurrentUserService currentUserService) {
		this.caseFacade = caseFacade;
		this.personFacade = personFacade;
		this.caseService = caseService;
		this.currentUserService = currentUserService;
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
		String uuid = draft.node().cell(CASE_UUID);
		if (!uuid.isEmpty()) {
			return Optional.of(uuid);
		}
		return Optional.of(draft.node().cell(CASE_EXTERNAL_ID)).filter(externalId -> !externalId.isEmpty());
	}

	@Override
	public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
		Set<String> uuids = drafts.stream().map(draft -> draft.node().cell(CASE_UUID)).filter(uuid -> !uuid.isEmpty()).collect(Collectors.toSet());
		Set<String> externalIds = drafts.stream()
			.filter(draft -> draft.node().cell(CASE_UUID).isEmpty())
			.map(draft -> draft.node().cell(CASE_EXTERNAL_ID))
			.filter(externalId -> !externalId.isEmpty())
			.collect(Collectors.toSet());
		Map<String, Case> byUuid = uuids.isEmpty()
			? Map.of()
			: caseService.getByUuids(new ArrayList<>(uuids))
				.stream()
				.filter(caze -> !caze.isDeleted())
				.collect(Collectors.toMap(Case::getUuid, Function.identity()));
		Map<String, List<Case>> byExternalId = caseService.getByExternalIds(externalIds).stream().collect(Collectors.groupingBy(Case::getExternalID));

		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		Map<ImportNode, Case> found = new IdentityHashMap<>();
		for (PlannedRecord draft : drafts) {
			ImportNode node = draft.node();
			String uuid = node.cell(CASE_UUID);
			if (!uuid.isEmpty()) {
				Case caze = byUuid.get(uuid);
				if (caze == null) {
					matches.put(node, Match.error(CASE_UUID, I18nProperties.getValidationError(Validations.importHierarchicalCaseNotFound, uuid)));
				} else {
					found.put(node, caze);
				}
				continue;
			}
			String externalId = node.cell(CASE_EXTERNAL_ID);
			List<Case> candidates = byExternalId.getOrDefault(externalId, List.of());
			if (candidates.size() > 1) {
				matches.put(
					node,
					Match.error(
						CASE_EXTERNAL_ID,
						I18nProperties.getValidationError(Validations.importHierarchicalExternalIdAmbiguous, externalId, candidates.size())));
			} else if (candidates.size() == 1) {
				found.put(node, candidates.get(0));
			}
		}
		matches.putAll(existingMatches(found));
		return matches;
	}

	private Map<ImportNode, Match> existingMatches(Map<ImportNode, Case> found) {
		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		Map<ImportNode, Case> editable = new IdentityHashMap<>();
		found.forEach((node, caze) -> {
			if (caseService.getEditPermissionType(caze) == EditPermissionType.ALLOWED) {
				editable.put(node, caze);
			} else {
				matches.put(node, Match.error(CASE_UUID, I18nProperties.getValidationError(Validations.importHierarchicalCaseNotEditable, caze.getUuid())));
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
			matches.put(node, Match.existing(caze.getUuid(), targets, Arrays.asList(caze.getChangeDate(), person.getChangeDate())));
		});
		return matches;
	}

	@Override
	public String label(PlannedRecord record) {
		PersonDto person = record.target(PERSON);
		CaseDataDto caze = record.target(CASE);
		String name = (Objects.toString(person.getFirstName(), "") + " " + Objects.toString(person.getLastName(), "")).strip();
		String id = caze.getExternalID() != null ? caze.getExternalID() : DataHelper.getShortUuid(caze.getUuid());
		return name.isEmpty() ? id : name + " (" + id + ")";
	}

	@Override
	public void complete(PlannedRecord record) {
		if (record.isNew() && !currentUserService.hasUserRight(UserRight.CASE_IMPORT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_IMPORT));
		}
		if (record.operation() == OperationType.UPDATE && !currentUserService.hasUserRight(UserRight.CASE_EDIT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.CASE_EDIT));
		}
		if (record.operation() == OperationType.UNCHANGED) {
			return;
		}
		if (MaskedRecords.isMasked(record)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMaskedRecord));
			return;
		}
		if (RejectedRecords.alreadyRejected(record)) {
			return;
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
