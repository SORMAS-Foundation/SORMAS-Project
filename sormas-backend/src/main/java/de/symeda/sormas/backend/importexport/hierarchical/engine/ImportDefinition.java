package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;

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
}
