package de.symeda.sormas.backend.patch;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause;

public enum PathFailureCause {

	FORBIDDEN_NON_UNIQUE_ALIAS(DataPatchFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS, PartialRetrievalFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS),
	UNSUPPORTED_PREFIX(DataPatchFailureCause.UNSUPPORTED_PREFIX, PartialRetrievalFailureCause.UNSUPPORTED_PREFIX),
	FORBIDDEN_FIELD(DataPatchFailureCause.FORBIDDEN_FIELD, PartialRetrievalFailureCause.FORBIDDEN_FIELD),
	INVALID_MULTIPLE_FIELDS_FORMAT(DataPatchFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT, PartialRetrievalFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT);

	@NotNull
	private final DataPatchFailureCause relatedPatchFailureCause;
	@NotNull
	private final PartialRetrievalFailureCause relatedRetrieveFailureCause;

	PathFailureCause(DataPatchFailureCause relatedPatchFailureCause, PartialRetrievalFailureCause relatedRetrieveFailureCause) {
		this.relatedPatchFailureCause = relatedPatchFailureCause;
		this.relatedRetrieveFailureCause = relatedRetrieveFailureCause;
	}

	public DataPatchFailureCause getRelatedPatchFailureCause() {
		return relatedPatchFailureCause;
	}

	public PartialRetrievalFailureCause getRelatedRetrieveFailureCause() {
		return relatedRetrieveFailureCause;
	}
}
