package de.symeda.sormas.ui.importer.hierarchical;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.MatchReason;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

public final class PreviewPresentation {

	private static final int MAX_GRID_ROWS = 8;

	private final List<ImportIssueDto> problems;
	private final List<ProblemRow> problemRows;
	private final List<ChangeRow> changes;
	private final List<RecordOperationDto> newRecords;

	public PreviewPresentation(ImportPreviewDto preview, ImportReportDto report, ColumnLabels labels) {
		this.problems = new ArrayList<>(preview.allIssues());
		report.getResults().forEach(result -> problems.addAll(result.getIssues()));
		TreeMap<Integer, BlockPreviewDto> blocksByFirstLine = new TreeMap<>();
		preview.getBlocks().forEach(block -> blocksByFirstLine.putIfAbsent(block.getFirstLine(), block));
		this.problemRows = problems.stream()
			.sorted(Comparator.comparing(ImportIssueDto::getLineNumber, Comparator.nullsLast(Comparator.naturalOrder())))
			.map(issue -> new ProblemRow(recordOf(blocksByFirstLine, issue), labels.label(issue.getColumn()), issue))
			.collect(Collectors.toList());
		this.changes = readyOperations(preview)
			.flatMap(
				operation -> operation.getChanges()
					.stream()
					.map(
						change -> new ChangeRow(
							operation.getLabel(),
							change.getField(),
							change.getOldValue() + " → " + change.getNewValue(),
							operation.getMatchReason(),
							operation.getLinkedLine())))
			.collect(Collectors.toList());
		this.newRecords = readyOperations(preview).filter(operation -> operation.getType() == OperationType.CREATE).collect(Collectors.toList());
	}

	public List<ImportIssueDto> problems() {
		return problems;
	}

	public List<ProblemRow> problemRows() {
		return problemRows;
	}

	public int problemCount() {
		return (int) problemRows.stream().filter(row -> !row.isHeldBack()).count();
	}

	public List<ChangeRow> changes() {
		return changes;
	}

	public List<RecordOperationDto> newRecords() {
		return newRecords;
	}

	public boolean hasProblems() {
		return !problems.isEmpty();
	}

	public boolean nothingToReview() {
		return problems.isEmpty() && changes.isEmpty();
	}

	public static String matchLabel(MatchReason reason, Integer linkedLine) {
		if (reason == null) {
			return "";
		}
		switch (reason) {
		case BY_ID:
			return I18nProperties.getCaption(Captions.hierarchicalImportMatchedById);
		case BY_SIMILARITY:
			return I18nProperties.getCaption(Captions.hierarchicalImportMatchedBySimilarity);
		case NEW_FOR_EXISTING_PERSON:
			return I18nProperties.getCaption(Captions.hierarchicalImportNewForExistingPerson);
		case NEW_FOR_PERSON_IN_FILE:
			return String.format(I18nProperties.getCaption(Captions.hierarchicalImportNewForPersonInFile), linkedLine);
		default:
			return I18nProperties.getCaption(Captions.hierarchicalImportNewCase);
		}
	}

	public static String ignoredColumnsMessage(ImportPreviewDto preview) {
		if (preview.getIgnoredColumns().isEmpty()) {
			return "";
		}
		return String.format(I18nProperties.getString(Strings.messageHierarchicalImportIgnoredColumns), String.join(", ", preview.getIgnoredColumns()));
	}

	public static int gridRows(int itemCount) {
		return Math.max(1, Math.min(itemCount, MAX_GRID_ROWS));
	}

	private static String recordOf(TreeMap<Integer, BlockPreviewDto> blocksByFirstLine, ImportIssueDto issue) {
		Integer line = issue.getLineNumber();
		if (line == null) {
			return "";
		}
		Map.Entry<Integer, BlockPreviewDto> entry = blocksByFirstLine.floorEntry(line);
		return entry != null && line <= entry.getValue().getLastLine() ? entry.getValue().getLabel() : "";
	}

	private static Stream<RecordOperationDto> readyOperations(ImportPreviewDto preview) {
		return preview.getBlocks()
			.stream()
			.filter(block -> block.getStatus() == BlockStatus.READY)
			.map(BlockPreviewDto::getOperations)
			.flatMap(List::stream);
	}

	public static final class ProblemRow {

		private final String record;
		private final String column;
		private final ImportIssueDto issue;

		ProblemRow(String record, String column, ImportIssueDto issue) {
			this.record = record;
			this.column = column;
			this.issue = issue;
		}

		public String getRecord() {
			return record;
		}

		public Integer getLineNumber() {
			return issue.getLineNumber();
		}

		public String getColumn() {
			return column;
		}

		public String getMessage() {
			return issue.getMessage();
		}

		public boolean isHeldBack() {
			return issue.getKind() == ImportIssueKind.SKIPPED;
		}
	}

	public static final class ChangeRow {

		private final String record;
		private final String field;
		private final String change;
		private final MatchReason matchReason;
		private final Integer linkedLine;

		ChangeRow(String record, String field, String change, MatchReason matchReason, Integer linkedLine) {
			this.record = record;
			this.field = field;
			this.change = change;
			this.matchReason = matchReason;
			this.linkedLine = linkedLine;
		}

		public Integer getLinkedLine() {
			return linkedLine;
		}

		public MatchReason getMatchReason() {
			return matchReason;
		}

		public String getRecord() {
			return record;
		}

		public String getField() {
			return field;
		}

		public String getChange() {
			return change;
		}
	}
}
