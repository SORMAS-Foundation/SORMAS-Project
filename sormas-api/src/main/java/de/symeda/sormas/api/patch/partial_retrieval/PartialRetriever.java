package de.symeda.sormas.api.patch.partial_retrieval;

/**
 * Can be used to retrieve partial data linked to a case.
 */
public interface PartialRetriever {

	/**
	 * Use this to fetch the actual values in their concrete types.
	 * 
	 * @param request
	 *            to configure retrieval.
	 * @return response with actual values in their concrete types and possibly errors.
	 */
	PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request);

	/**
	 * Use this to fetch the actual values in displayable string format.
	 *
	 * @param request
	 *            to configure retrieval.
	 * @return response with actual values in displayable string format and possibly translated errors.
	 */
	DisplayablePartialRetrievalResponse retrievePartialForDisplay(PartialRetrievalRequest request);
}
