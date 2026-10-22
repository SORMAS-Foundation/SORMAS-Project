package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockResultDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

public final class ImportExecutor {

	public ImportReportDto commit(ImportDefinition definition, ImportSheet sheet, Map<Integer, String> previewedFingerprints, BlockRunner runner) {
		ImportPlan plan = new Planner().plan(definition, sheet, previewedFingerprints.keySet());
		if (plan.hasFileIssues()) {
			return new ImportReportDto(
				previewedFingerprints.keySet()
					.stream()
					.map(index -> new BlockResultDto(index, BlockOutcome.FAILED, plan.fileIssues()))
					.collect(Collectors.toList()));
		}
		List<BlockResultDto> results = new ArrayList<>();
		Set<ImportNode> committedRoots = new HashSet<>();
		for (PlannedBlock block : plan.blocks()) {
			BlockResultDto result = linkedRootMissing(block, committedRoots)
				? linkedRootNotImported(block)
				: commitBlock(definition, block, previewedFingerprints.get(block.index()), runner);
			if (result.getOutcome() == BlockOutcome.COMMITTED) {
				committedRoots.add(block.root());
			}
			results.add(result);
		}
		return new ImportReportDto(results);
	}

	private static boolean linkedRootMissing(PlannedBlock block, Set<ImportNode> committedRoots) {
		PlannedRecord root = block.rootRecord();
		return root != null && root.linkedRoot() != null && !committedRoots.contains(root.linkedRoot());
	}

	private static BlockResultDto linkedRootNotImported(PlannedBlock block) {
		ImportIssueDto issue = new ImportIssueDto(
			ImportIssueKind.COMMIT,
			block.root().lineNumber(),
			null,
			I18nProperties.getValidationError(Validations.importHierarchicalLinkedPersonNotImported, block.rootRecord().linkedRoot().lineNumber()));
		return new BlockResultDto(block.index(), BlockOutcome.FAILED, List.of(issue));
	}

	private static BlockResultDto commitBlock(ImportDefinition definition, PlannedBlock block, String previewedFingerprint, BlockRunner runner) {
		if (block.status() == BlockStatus.ERROR) {
			return new BlockResultDto(block.index(), BlockOutcome.FAILED, block.issues());
		}
		if (!block.fingerprint().equals(previewedFingerprint)) {
			List<ImportIssueDto> issues = new ArrayList<>(block.issues());
			issues.add(
				new ImportIssueDto(
					ImportIssueKind.COMMIT,
					block.root().lineNumber(),
					null,
					I18nProperties.getValidationError(Validations.importHierarchicalChangedSincePreview)));
			return new BlockResultDto(block.index(), BlockOutcome.CHANGED_SINCE_PREVIEW, issues);
		}
		if (block.status() == BlockStatus.UNCHANGED) {
			return new BlockResultDto(block.index(), BlockOutcome.UNCHANGED, List.of());
		}
		try {
			runner.run(() -> definition.committer().commit(block));
			return new BlockResultDto(block.index(), BlockOutcome.COMMITTED, List.of());
		} catch (RuntimeException e) {
			ImportIssueDto failure = new ImportIssueDto(ImportIssueKind.COMMIT, block.root().lineNumber(), null, rootMessage(e));
			return new BlockResultDto(block.index(), BlockOutcome.FAILED, List.of(failure));
		}
	}

	static String rootMessage(Throwable throwable) {
		Throwable cause = throwable;
		while (cause.getCause() != null && cause.getCause() != cause) {
			cause = cause.getCause();
		}
		return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
	}
}
