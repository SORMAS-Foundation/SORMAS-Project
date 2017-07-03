package de.symeda.auditlog.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Identifies the transaction in which a change has happened.
 * 
 * @author Oliver Milke
 * @since 15.01.2016
 */
public class TransactionId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionId = Integer.toString(new Date().hashCode());

	public String getTransactionId() {
		return transactionId;
	}

}
