package de.symeda.sormas.api.utils;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback = true)
public class ValidationRuntimeException extends RuntimeException {

	public ValidationRuntimeException(String message) {
		super(message);
	}
	
}
