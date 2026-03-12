package de.symeda.sormas.api.patch.partial_retrieval;

public interface PartialRetriever {

	/**
	 * TODO:
	 * - Reuse the single FieldPatcher for that purpose ? Create additional interface to retrieve the value for the supported field.
	 * - I18n using Field ID: must be retrieved by the
	 * - Field type using Property accessor: should be done in one shot to avoid too much
	 *
	 *
	 * @param request
	 * @return
	 */
	PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request);

	DisplayablePartialRetrievalResponse retrievePartialForDisplay(PartialRetrievalRequest request);
}
