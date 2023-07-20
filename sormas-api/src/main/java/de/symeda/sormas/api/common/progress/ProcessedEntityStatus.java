package de.symeda.sormas.api.common.progress;

public enum ProcessedEntityStatus {

	SUCCESS,
	/**
	 * ExternalSurveillanceToolRuntimeException was thrown during the processing of the entity
	 */
	EXTERNAL_SURVEILLANCE_FAILURE,
	/**
	 * SormasToSormasRuntimeException was thrown during the processing of the entity
	 */
	SORMAS_TO_SORMAS_FAILURE,
	/**
	 * AccessDeniedException was thrown during the processing of the entity
	 */
	ACCESS_DENIED_FAILURE,
	/**
	 * An internal exception was thrown during the processing of the entity
	 */
	INTERNAL_FAILURE;

}
