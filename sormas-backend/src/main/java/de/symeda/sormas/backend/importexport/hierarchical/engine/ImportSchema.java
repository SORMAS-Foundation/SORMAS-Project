package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.Optional;

public final class ImportSchema {

	private final List<ImportLevel> levels;

	public ImportSchema(ImportLevel... levels) {
		this.levels = List.of(levels);
	}

	public List<ImportLevel> levels() {
		return levels;
	}

	public Optional<ImportLevel> levelOf(String header) {
		return levels.stream().filter(level -> level.owns(header)).findFirst();
	}
}
