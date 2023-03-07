package de.symeda.sormas.api.utils;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class BulkOperationResults<E> implements Serializable {

	private final int processedEntries;
	private final long elapsedTime;
	private final List<E> remainingEntries;

	public BulkOperationResults(int processedEntries, long elapsedTime, List<E> remainingUuids) {
		this.processedEntries = processedEntries;
		this.elapsedTime = elapsedTime;
		this.remainingEntries = remainingUuids;
	}

	public int getProcessedEntries() {
		return processedEntries;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public List<E> getRemainingEntries() {
		return remainingEntries;
	}

	public boolean hasReachedTimeLimit() {
		return elapsedTime >= DataHelper.BULK_EDIT_TIME_LIMIT;
	}

	public boolean hasReachedEntryLimit() {
		return processedEntries >= DataHelper.BULK_EDIT_ENTRY_LIMIT;
	}
}
