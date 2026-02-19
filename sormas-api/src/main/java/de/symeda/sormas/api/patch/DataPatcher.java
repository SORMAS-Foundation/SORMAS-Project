package de.symeda.sormas.api.patch;

public interface DataPatcher {

	/**
	 * Allow patching data for a specific case.
	 * 
	 * @param request
	 *            instructions for the data patch
	 * @return response that indicates
	 */
	DataPatchResponse patch(DataPatchRequest request);
}
