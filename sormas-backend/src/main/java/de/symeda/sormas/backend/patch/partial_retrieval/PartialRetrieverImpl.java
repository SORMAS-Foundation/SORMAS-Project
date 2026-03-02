package de.symeda.sormas.backend.patch.partial_retrieval;

import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalRequest;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalResponse;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetriever;

public class PartialRetrieverImpl implements PartialRetriever {

	@Override
	public PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request) {
		/*
		 * Implementation steps:
		 * - Iterate over fields
		 * - Validate if allowed
		 * - Un-alias to get physical for Reflection
		 * - Alias to get Field ID for I18N
		 * - Get type
		 * - Get value
		 */
		return null;
	}
}
