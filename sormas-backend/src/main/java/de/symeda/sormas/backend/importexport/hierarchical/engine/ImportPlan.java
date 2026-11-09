package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;

public final class ImportPlan {

	private final List<ImportIssueDto> fileIssues;
	private final List<ImportIssueDto> rowIssues;
	private final List<PlannedBlock> blocks;
	private final List<String> ignoredColumns;
	private final Map<ImportLevel, LevelHandler> handlers;

	ImportPlan(
		List<ImportIssueDto> fileIssues,
		List<ImportIssueDto> rowIssues,
		List<PlannedBlock> blocks,
		List<String> ignoredColumns,
		Map<ImportLevel, LevelHandler> handlers) {
		this.ignoredColumns = ignoredColumns;
		this.handlers = handlers;
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

	LevelHandler handler(ImportLevel level) {
		return handlers.get(level);
	}

	public boolean hasFileIssues() {
		return !fileIssues.isEmpty();
	}
}
