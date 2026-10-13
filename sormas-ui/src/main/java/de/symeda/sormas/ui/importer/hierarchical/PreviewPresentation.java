package de.symeda.sormas.ui.importer.hierarchical;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.importexport.hierarchical.BlockPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;

public final class PreviewPresentation {

	private static final int MAX_GRID_ROWS = 8;

	private final List<ImportIssueDto> problems;
	private final List<ChangeRow> changes;
	private final List<RecordOperationDto> newRecords;

	public PreviewPresentation(ImportPreviewDto preview, ImportReportDto report) {
		this.problems = new ArrayList<>(preview.allIssues());
		report.getResults().forEach(result -> problems.addAll(result.getIssues()));
		this.changes = readyOperations(preview)
			.flatMap(
				operation -> operation.getChanges()
					.stream()
					.map(change -> new ChangeRow(operation.getLabel(), change.getField(), change.getOldValue() + " → " + change.getNewValue())))
			.collect(Collectors.toList());
		this.newRecords = readyOperations(preview).filter(operation -> operation.getType() == OperationType.CREATE).collect(Collectors.toList());
	}

	public List<ImportIssueDto> problems() {
		return problems;
	}

	public List<ChangeRow> changes() {
		return changes;
	}

	public List<RecordOperationDto> newRecords() {
		return newRecords;
	}

	public boolean nothingToReview() {
		return problems.isEmpty() && changes.isEmpty();
	}

	public static int gridRows(int itemCount) {
		return Math.max(1, Math.min(itemCount, MAX_GRID_ROWS));
	}

	private static Stream<RecordOperationDto> readyOperations(ImportPreviewDto preview) {
		return preview.getBlocks()
			.stream()
			.filter(block -> block.getStatus() == BlockStatus.READY)
			.map(BlockPreviewDto::getOperations)
			.flatMap(List::stream);
	}

	public static final class ChangeRow {

		private final String record;
		private final String field;
		private final String change;

		ChangeRow(String record, String field, String change) {
			this.record = record;
			this.field = field;
			this.change = change;
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
