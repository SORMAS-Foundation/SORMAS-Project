package de.symeda.sormas.api.utils;

import javax.ejb.ApplicationException;

/*
 * ATTENTION: Does not do a rollback when thrown because this class is used in
 * case import where no rollback may be done (in order to continue with the import
 * when validation of a single case fails).
 * 
 * Make sure to call this before changing backend data (e.g. when using it to
 * validate transfered cases).
 */
@SuppressWarnings("serial")
@ApplicationException(rollback = false)
public class ValidationRuntimeException extends RuntimeException {

	public ValidationRuntimeException(String message) {
		super(message);
	}
	
}
