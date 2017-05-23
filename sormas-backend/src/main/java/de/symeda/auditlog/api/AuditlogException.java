package de.symeda.auditlog.api;

/**
 * @author Stefan Kock
 */
public class AuditlogException extends RuntimeException {

	private static final long serialVersionUID = -6278141238374863137L;

	public AuditlogException(String message) {
		super(message);
	}

	public AuditlogException(String message, Throwable cause) {
		super(message, cause);
	}

}
