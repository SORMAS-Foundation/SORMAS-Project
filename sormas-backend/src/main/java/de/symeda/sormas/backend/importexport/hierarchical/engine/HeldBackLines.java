package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;

final class HeldBackLines {

	private HeldBackLines() {
	}

	static List<ImportIssueDto> of(ImportNode root, Collection<ImportIssueDto> problems) {
		if (problems.isEmpty()) {
			return List.of();
		}
		Set<Integer> problemLines = problems.stream().map(ImportIssueDto::getLineNumber).filter(Objects::nonNull).collect(Collectors.toSet());
		Integer firstProblemLine = problems.iterator().next().getLineNumber();
		int reportedLine = firstProblemLine != null ? firstProblemLine : root.lineNumber();
		String message = I18nProperties.getValidationError(Validations.importHierarchicalLineHeldBack, reportedLine);
		return root.subtree()
			.map(ImportNode::lineNumber)
			.distinct()
			.filter(line -> !problemLines.contains(line))
			.map(line -> new ImportIssueDto(ImportIssueKind.SKIPPED, line, null, message))
			.collect(Collectors.toList());
	}
}
