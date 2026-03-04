package de.symeda.sormas.api.patch;

/**
 * Allows to partially patch data from a case.
 */
public interface DataPatcher {

	/**
	 * Allow patching data for a specific case.
	 * 
	 * @param request
	 *            instructions for the data patch
	 * @return response that indicates
	 */
	DataPatchResponse patch(CaseDataPatchRequest request);
}
