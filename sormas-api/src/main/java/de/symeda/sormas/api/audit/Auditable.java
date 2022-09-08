package de.symeda.sormas.api.audit;

import de.symeda.sormas.api.HasUuid;

public interface Auditable extends HasUuid {

	default String getAuditRepresentation() {
		return getUuid();
	}
}
