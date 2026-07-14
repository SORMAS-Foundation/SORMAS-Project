package de.symeda.sormas.backend.patch;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause;

public enum PropertyAccessFailure {

	INVALID_INPUT(DataPatchFailureCause.TECHNICAL, PartialRetrievalFailureCause.TECHNICAL),
	FIELD_DOES_NOT_EXIST(DataPatchFailureCause.FIELD_DOES_NOT_EXIST, PartialRetrievalFailureCause.FIELD_DOES_NOT_EXIST),
	UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE(DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE,
		PartialRetrievalFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE);

	@NotNull
	private final DataPatchFailureCause relatedPatchFailureCause;
	@NotNull
	private final PartialRetrievalFailureCause relatedRetrieveFailureCause;

	PropertyAccessFailure(DataPatchFailureCause relatedPatchFailureCause, PartialRetrievalFailureCause relatedRetrieveFailureCause) {
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
