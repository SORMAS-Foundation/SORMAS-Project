package de.symeda.sormas.backend.importexport.hierarchical.caze;

import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;

final class MaskedRecords {

	private MaskedRecords() {
	}

	static boolean isMasked(PlannedRecord record) {
		return record.original() != null
			&& record.original().values().stream().anyMatch(target -> target instanceof Pseudonymizable && ((Pseudonymizable) target).isPseudonymized());
	}
}
