package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;

public final class ImportPlan {

	private final List<ImportIssueDto> fileIssues;
	private final List<ImportIssueDto> rowIssues;
	private final List<PlannedBlock> blocks;

	public ImportPlan(List<ImportIssueDto> fileIssues, List<ImportIssueDto> rowIssues, List<PlannedBlock> blocks) {
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

	public boolean hasFileIssues() {
		return !fileIssues.isEmpty();
	}
}
