package de.symeda.sormas.backend.importexport.hierarchical.caze;

import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;

final class RejectedRecords {

	private RejectedRecords() {
	}

	static boolean alreadyRejected(PlannedRecord record) {
		return !record.node().issues().isEmpty()
			|| record.node().children().stream().anyMatch(child -> child.level().isEmbedded() && !child.issues().isEmpty());
	}
}
