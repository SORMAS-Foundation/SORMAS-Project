package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.Objects;
import java.util.stream.Stream;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;

final class RejectedRecords {

	private RejectedRecords() {
	}

	static boolean hasAnyIssue(PlannedRecord record) {
		return issuesOf(record.node()).findAny().isPresent();
	}

	static boolean hasIssuesWithoutColumn(PlannedRecord record) {
		return issuesOf(record.node()).anyMatch(issue -> issue.getColumn() == null);
	}

	static Stream<String> columnsWithIssues(ImportNode node) {
		return issuesOf(node).map(ImportIssueDto::getColumn).filter(Objects::nonNull);
	}

	private static Stream<ImportIssueDto> issuesOf(ImportNode node) {
		return Stream.concat(Stream.of(node), node.children().stream().filter(child -> child.level().isEmbedded()))
			.flatMap(scoped -> scoped.issues().stream());
	}
}
