package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.SAMPLE;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.TEST;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.LevelHandler;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Match;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.user.CurrentUserService;

final class TestHandler implements LevelHandler {

	private final PathogenTestFacadeEjbLocal pathogenTestFacade;
	private final CurrentUserService currentUserService;
	private final AstFolder astFolder;

	TestHandler(PathogenTestFacadeEjbLocal pathogenTestFacade, CurrentUserService currentUserService, AstFolder astFolder) {
		this.pathogenTestFacade = pathogenTestFacade;
		this.currentUserService = currentUserService;
		this.astFolder = astFolder;
	}

	@Override
	public Map<String, Object> newTargets(ImportNode node, PlannedRecord parent) {
		SampleDto sample = parent.target(SAMPLE);
		CaseDataDto caze = parent.parent().target(CASE);
		PathogenTestDto test = PathogenTestDto.build(sample.toReference(), currentUserService.getCurrentUser().toReference());
		test.setLab(sample.getLab());
		test.setLabDetails(sample.getLabDetails());
		test.setTestedDisease(caze.getDisease());
		Map<String, Object> targets = new HashMap<>();
		targets.put(TEST, test);
		return targets;
	}

	@Override
	public Optional<String> key(PlannedRecord draft) {
		return Optional.of(signature(draft.target(TEST)));
	}

	@Override
	public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
		List<String> sampleUuids = drafts.stream()
			.filter(draft -> !draft.parent().isNew())
			.map(draft -> draft.parent().<SampleDto> target(SAMPLE).getUuid())
			.distinct()
			.collect(Collectors.toList());
		Map<String, List<PathogenTestDto>> testsBySample = sampleUuids.isEmpty()
			? Map.of()
			: pathogenTestFacade.getBySampleUuids(sampleUuids).stream().collect(Collectors.groupingBy(test -> test.getSample().getUuid()));

		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		for (PlannedRecord draft : drafts) {
			if (draft.parent().isNew()) {
				continue;
			}
			String signature = signature(draft.target(TEST));
			testsBySample.getOrDefault(draft.parent().<SampleDto> target(SAMPLE).getUuid(), List.of())
				.stream()
				.filter(test -> signature(test).equals(signature))
				.findFirst()
				.ifPresent(test -> {
					Map<String, Object> targets = new HashMap<>();
					targets.put(TEST, test);
					matches.put(draft.node(), Match.existing(test.getUuid(), targets, Collections.singletonList(test.getChangeDate())));
				});
		}
		return matches;
	}

	private static String signature(PathogenTestDto test) {
		return test.getTestType() + "|" + test.getTestedDisease() + "|"
			+ (test.getTestDateTime() == null ? "" : test.getTestDateTime().getTime()) + "|" + test.getTestResult();
	}

	@Override
	public String label(PlannedRecord record) {
		PathogenTestDto test = record.target(TEST);
		return Objects.toString(test.getTestType(), "") + ", " + Objects.toString(test.getTestResult(), "");
	}

	@Override
	public void complete(PlannedRecord record) {
		astFolder.fold(record);
		if (record.isNew() && !currentUserService.hasUserRight(UserRight.PATHOGEN_TEST_CREATE)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.PATHOGEN_TEST_CREATE));
		}
		if (record.operation() == OperationType.UPDATE && !currentUserService.hasUserRight(UserRight.PATHOGEN_TEST_EDIT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.PATHOGEN_TEST_EDIT));
		}
		if (record.operation() == OperationType.UNCHANGED) {
			return;
		}
		try {
			pathogenTestFacade.validate(record.target(TEST));
		} catch (ValidationRuntimeException e) {
			record.reportIssue(null, e.getMessage());
		}
	}
}
