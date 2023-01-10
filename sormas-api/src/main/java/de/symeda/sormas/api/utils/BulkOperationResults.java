package de.symeda.sormas.api.utils;

import java.io.Serializable;
import java.util.List;

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

	public boolean hasExceededTimeLimit() {
		return elapsedTime > DataHelper.BULK_EDIT_TIME_LIMIT;
	}

	public boolean hasExceededEntryLimit() {
		return processedEntries > DataHelper.BULK_EDIT_ENTRY_LIMIT;
	}
}
