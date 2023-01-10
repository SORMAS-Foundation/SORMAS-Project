package de.symeda.sormas.backend.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.utils.BulkOperationResults;

public class BulkOperationHelper {

	private BulkOperationHelper() {

	}

	public static <E> BulkOperationResults<E> executeWithLimits(List<E> entries, long timeLimit, int entryLimit, Function<E, Boolean> batchFunction) {

		List<E> remainingEntries = new ArrayList<>(entries);
		int processedEntries = 0;
		long elapsedTime = 0;
		Instant executionStart = Instant.now();

		for (E entry : entries) {
			boolean processed = batchFunction.apply(entry);
			if (processed) {
				processedEntries++;
				remainingEntries.remove(entry);
			}
			elapsedTime = Duration.between(executionStart, Instant.now()).toMillis();

			if (processedEntries >= entryLimit || elapsedTime >= timeLimit) {
				break;
			}
		}

		return new BulkOperationResults<>(processedEntries, elapsedTime, remainingEntries);
	}
}
