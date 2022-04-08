package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

/**
 * Helper methods concerning Iterables (Arrays, Collections).
 * 
 * @author stefan.kock
 */
public final class IterableHelper {

	private IterableHelper() {
		// Hide utility class constructor
	}

	/**
	 * Splits the {@code entries} into batches and executes a function for each batch.
	 * 
	 * @param <E>
	 *            Type of batch entries.
	 * @param entries
	 *            total amount of entries to process.
	 * @param batchSize
	 *            maximum number of entries in one batch.
	 * @param batchFunction
	 *            The function to call for a batch.
	 */
	public static <E> void executeBatched(List<E> entries, int batchSize, Consumer<List<E>> batchFunction) {

		if (CollectionUtils.isNotEmpty(entries)) {
			for (List<E> batch : ListUtils.partition(new ArrayList<>(entries), batchSize)) {
				if (CollectionUtils.isNotEmpty(batch)) {
					batchFunction.accept(batch);
				}
			}
		}
	}
}
