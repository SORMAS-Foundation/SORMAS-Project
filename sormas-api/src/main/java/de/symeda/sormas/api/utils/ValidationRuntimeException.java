package de.symeda.sormas.api.utils;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback = false)
public class ValidationRuntimeException extends RuntimeException {

	public ValidationRuntimeException(String message) {
		super(message);
	}
	
}
