package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;

public final class GroupingResult {

	private final List<ImportNode> roots;
	private final List<ImportIssueDto> issues;

	public GroupingResult(List<ImportNode> roots, List<ImportIssueDto> issues) {
		this.roots = roots;
		this.issues = issues;
	}

	public List<ImportNode> roots() {
		return roots;
	}

	public List<ImportIssueDto> issues() {
		return issues;
	}
}
