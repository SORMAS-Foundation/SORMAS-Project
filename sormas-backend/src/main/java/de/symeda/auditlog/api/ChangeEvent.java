package de.symeda.auditlog.api;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event object that describes the changes of an entity.
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
	 * Returns the {@link TransactionId} to describe the current transaction (user + transaction).
	 * @return
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Returns the {@link EntityId} to describe the changes entity.
	 * @return
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
	 * Returns the list of all changed attributes with the <code>key</code> of the map being the name of the changed attribute
	 * and the <code>value</code> being its new value.
	 * @return
	 */
	public Map<String, String> getNewValues() {
		return newValues;
	}

}
