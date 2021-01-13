package de.symeda.sormas.backend.util;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.After;
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

		// 1. No execution on empty collection
//		assertExecuteCount(batchSize, 1, 1);
		assertExecuteCount(batchSize, 1, 1, 2);
		assertExecuteCount(batchSize, 1, 1, 2, 3);

		// 2. Testing with several batches
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4);
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4, 5);
		assertExecuteCount(batchSize, 2, 1, 2, 3, 4, 5, 6);
		assertExecuteCount(batchSize, 3, 1, 2, 3, 4, 5, 6, 7);
		assertExecuteCount(5, 2, 1, 2, 3, 4, 5, 6);
	}

	@SuppressWarnings("unchecked")
	private void assertExecuteCount(int batchSize, int executions, Integer... entries) {

		Consumer<List<Integer>> batchFunction = mock(Consumer.class);

		List<Integer> asList = Arrays.asList(entries);
		System.out.println("entries: " + entries);
		System.out.println("asList:" + asList);
		System.out.println("batchFunction:" + batchFunction);
		IterableHelper.executeBatched(asList, batchSize, batchFunction);
		verify(batchFunction, times(executions)).accept(anyList());
	}

	@After
	public void validate() {
//		validateMockitoUsage();
	}
}
