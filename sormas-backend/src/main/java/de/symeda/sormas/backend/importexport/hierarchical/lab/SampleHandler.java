package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.CASE;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.SAMPLE;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.LevelHandler;
import de.symeda.sormas.backend.importexport.hierarchical.engine.Match;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.CurrentUserService;

final class SampleHandler implements LevelHandler {

	private final SampleFacadeEjbLocal sampleFacade;
	private final CurrentUserService currentUserService;

	SampleHandler(SampleFacadeEjbLocal sampleFacade, CurrentUserService currentUserService) {
		this.sampleFacade = sampleFacade;
		this.currentUserService = currentUserService;
	}

	@Override
	public Map<String, Object> newTargets(ImportNode node, PlannedRecord parent) {
		CaseDataDto caze = parent.target(CASE);
		SampleDto sample = SampleDto.build(currentUserService.getCurrentUser().toReference(), caze.toReference());
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		Map<String, Object> targets = new HashMap<>();
		targets.put(SAMPLE, sample);
		return targets;
	}

	@Override
	public Optional<String> key(PlannedRecord draft) {
		return Optional.ofNullable(draft.<SampleDto> target(SAMPLE).getFieldSampleID()).filter(StringUtils::isNotBlank);
	}

	@Override
	public Map<ImportNode, Match> match(List<PlannedRecord> drafts) {
		List<String> caseUuids = drafts.stream()
			.filter(draft -> !draft.parent().isNew())
			.map(draft -> draft.parent().<CaseDataDto> target(CASE).getUuid())
			.distinct()
			.collect(Collectors.toList());
		Map<String, SampleDto> existing = caseUuids.isEmpty()
			? Map.of()
			: sampleFacade.getByCaseUuids(caseUuids)
				.stream()
				.filter(sample -> StringUtils.isNotBlank(sample.getFieldSampleID()))
				.collect(Collectors.toMap(sample -> sample.getAssociatedCase().getUuid() + "/" + sample.getFieldSampleID(), Function.identity(), (a, b) -> a));

		Map<ImportNode, Match> matches = new IdentityHashMap<>();
		for (PlannedRecord draft : drafts) {
			Optional<String> fieldSampleId = key(draft);
			if (draft.parent().isNew() || fieldSampleId.isEmpty()) {
				continue;
			}
			SampleDto sample = existing.get(draft.parent().<CaseDataDto> target(CASE).getUuid() + "/" + fieldSampleId.get());
			if (sample != null) {
				Map<String, Object> targets = new HashMap<>();
				targets.put(SAMPLE, sample);
				matches.put(draft.node(), Match.existing(sample.getUuid(), targets, Collections.singletonList(sample.getChangeDate())));
			}
		}
		return matches;
	}

	@Override
	public String label(PlannedRecord record) {
		SampleDto sample = record.target(SAMPLE);
		if (StringUtils.isNotBlank(sample.getFieldSampleID())) {
			return sample.getFieldSampleID();
		}
		return sample.getSampleMaterial() != null ? sample.getSampleMaterial().toString() : DataHelper.getShortUuid(sample.getUuid());
	}

	@Override
	public void complete(PlannedRecord record) {
		if (record.isNew() && !currentUserService.hasUserRight(UserRight.SAMPLE_CREATE)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.SAMPLE_CREATE));
		}
		if (record.operation() == OperationType.UPDATE && !currentUserService.hasUserRight(UserRight.SAMPLE_EDIT)) {
			record.reportIssue(null, I18nProperties.getValidationError(Validations.importHierarchicalMissingRight, UserRight.SAMPLE_EDIT));
		}
		if (record.operation() == OperationType.UNCHANGED) {
			return;
		}
		try {
			sampleFacade.validate(record.target(SAMPLE), false);
		} catch (ValidationRuntimeException e) {
			record.reportIssue(null, e.getMessage());
		}
	}
}
