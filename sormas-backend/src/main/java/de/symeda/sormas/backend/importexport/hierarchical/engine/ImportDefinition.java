package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.Optional;

import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

public interface ImportDefinition {

	String id();

	ImportSchema schema();

	List<ColumnSpec> columns();

	CellWriter cellWriter();

	LevelHandler handler(ImportLevel level);

	BlockCommitter committer();

	default int maxRows() {
		return 10_000;
	}

	default List<ImportIssueDto> fileIssues(ImportSheet sheet) {
		return List.of();
	}

	default Optional<ColumnSpec> resolveColumn(String header) {
		return Optional.empty();
	}

	default boolean ignoresUnknownColumns() {
		return false;
	}
}
