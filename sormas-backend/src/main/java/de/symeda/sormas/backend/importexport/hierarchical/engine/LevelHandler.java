package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LevelHandler {

	Map<String, Object> newTargets(ImportNode node, PlannedRecord parent);

	Optional<String> key(PlannedRecord draft);

	Map<ImportNode, Match> match(List<PlannedRecord> drafts);

	String label(PlannedRecord record);

	void complete(PlannedRecord record);
}
