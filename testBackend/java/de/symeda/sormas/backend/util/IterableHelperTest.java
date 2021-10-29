package de.symeda.sormas.backend.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * @see IterableHelper
 * @author stefan.kock
 */
public class IterableHelperTest {

	@Test
	public void testExecuteBatched() {

		int batchSize = 3;

		// 0. No execution on empty collection
		assertExecuteCount(batchSize, 0);

		// 1. No execution on empty collection
		assertExecuteCount(batchSize, 1, 1);
		assertExecuteCount(batchSize, 1, 1, 2);
		assertExecuteCount(batchSize, 1, 1, 2, 3);

		// 2. Testing with several batches
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4);
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4, 5);
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4, 5, 6);
		assertExecuteCount(batchSize, 3, 1, 2, 3, 4, 5, 6, 7);
		assertExecuteCount(5, 2, 1, 2, 3, 4, 5, 6);
	}

	private static void assertExecuteCount(int batchSize, int executions, Integer... entries) {

		// Workaround instead of mocking because the mocked instance resulted in an NPE on Github CI.
		CountCalls<Integer> batchFunction = new CountCalls<>();

		IterableHelper.executeBatched(Arrays.asList(entries), batchSize, batchFunction);
		assertThat(batchFunction.counter, equalTo(executions));
	}

	private static class CountCalls<E> implements Consumer<List<E>> {

		private int counter;

		@Override
		public void accept(List<E> t) {
			counter++;
		}
	}
}
