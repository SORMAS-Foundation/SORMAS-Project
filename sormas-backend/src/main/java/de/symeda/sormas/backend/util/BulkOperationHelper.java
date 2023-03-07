package de.symeda.sormas.backend.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.symeda.sormas.api.utils.BulkOperationResults;
import de.symeda.sormas.api.utils.DataHelper;

public class BulkOperationHelper {

	private BulkOperationHelper() {

	}

	public static <E> BulkOperationResults<E> executeWithLimits(List<E> entries, Consumer<E> batchFunction) {

		List<E> remainingEntries = new ArrayList<>(entries);
		int processedEntries = 0;
		long elapsedTime = 0;
		Instant executionStart = Instant.now();

		for (E entry : entries) {
			batchFunction.accept(entry);
			processedEntries++;
			remainingEntries.remove(entry);
			elapsedTime = Duration.between(executionStart, Instant.now()).toMillis();

			if (processedEntries >= DataHelper.BULK_EDIT_ENTRY_LIMIT || elapsedTime >= DataHelper.BULK_EDIT_TIME_LIMIT) {
				break;
			}
		}

		return new BulkOperationResults<>(processedEntries, elapsedTime, remainingEntries);
	}
}
