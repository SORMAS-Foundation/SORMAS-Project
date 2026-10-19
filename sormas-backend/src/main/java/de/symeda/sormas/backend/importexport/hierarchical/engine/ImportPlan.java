package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;

public final class ImportPlan {

	private final List<ImportIssueDto> fileIssues;
	private final List<ImportIssueDto> rowIssues;
	private final List<PlannedBlock> blocks;
	private final List<String> ignoredColumns;

	public ImportPlan(List<ImportIssueDto> fileIssues, List<ImportIssueDto> rowIssues, List<PlannedBlock> blocks) {
		this(fileIssues, rowIssues, blocks, List.of());
	}

	public ImportPlan(List<ImportIssueDto> fileIssues, List<ImportIssueDto> rowIssues, List<PlannedBlock> blocks, List<String> ignoredColumns) {
		this.ignoredColumns = ignoredColumns;
		this.fileIssues = fileIssues;
		this.rowIssues = rowIssues;
		this.blocks = blocks;
	}

	public List<ImportIssueDto> fileIssues() {
		return fileIssues;
	}

	public List<ImportIssueDto> rowIssues() {
		return rowIssues;
	}

	public List<PlannedBlock> blocks() {
		return blocks;
	}

	public List<String> ignoredColumns() {
		return ignoredColumns;
	}

	public boolean hasFileIssues() {
		return !fileIssues.isEmpty();
	}
}
