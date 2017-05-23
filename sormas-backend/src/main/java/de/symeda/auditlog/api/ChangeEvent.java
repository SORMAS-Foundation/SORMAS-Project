package de.symeda.auditlog.api;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event-Objekt, das die Änderung eines Entities beschreibt.
 * 
 * @author Oliver Milke
 * @since 13.01.2016
 */
public class ChangeEvent {

	private final EntityId oid;
	private final Map<String, String> newValues;
	private final ChangeType changeType;
	private final LocalDateTime changeDate;
	private final String userId;
	private final String transactionId;

	public ChangeEvent(Map<String, String> newValues, ChangeType changeType) {

		this.oid = null;
		this.newValues = newValues;
		this.changeType = changeType;
		this.changeDate = null;
		this.userId = null;
		this.transactionId = null;
	}

	public ChangeEvent(ChangeEvent changeEvent, EntityId oid, LocalDateTime changeDate, UserId userId, TransactionId transactionId) {

		this.oid = oid;
		this.newValues = changeEvent.getNewValues();
		this.changeType = changeEvent.getChangeType();
		this.changeDate = changeDate;
		this.userId = userId.getName();
		this.transactionId = transactionId.getTransactionId();
	}

	public ChangeEvent(EntityId oid, Map<String, String> newValues, ChangeType changeType, LocalDateTime changeDate, UserId userId,
		TransactionId transactionId) {

		this.oid = oid;
		this.newValues = newValues;
		this.changeType = changeType;
		this.changeDate = changeDate;
		this.userId = userId.getName();
		this.transactionId = transactionId.getTransactionId();
	}

	/**
	 * Liefert die {@link TransactionId} zur Beschreibung des aktuellen Transaktion (Benutzer + Transaktion).
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Liefert die {@link EntityId} zur Beschreibung des geänderten Entities.
	 */
	public EntityId getOid() {
		return oid;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public LocalDateTime getChangeDate() {
		return changeDate;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * Liefert die Auflistung aller geänderten Attribute, wobei der <code>key</code> der Map der Name des geänderten Attributs
	 * ist und der <code>value</code> der neue Wert des Attributs ist.
	 */
	public Map<String, String> getNewValues() {
		return newValues;
	}

}
