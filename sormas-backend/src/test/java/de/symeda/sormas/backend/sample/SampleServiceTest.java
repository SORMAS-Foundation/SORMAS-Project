package de.symeda.sormas.backend.sample;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;

/**
 * @see SampleService
 */
public class SampleServiceTest extends AbstractBeanTest {

	@Test
	public void testGetNewTestResultCountByResultTypeVariousInClauseCount() {

		SampleService cut = getBean(SampleService.class);

		// 0. Works for 0 cases
		assertThat(cut.getNewTestResultCountByResultType(Collections.emptyList()).entrySet(), is(empty()));
		assertThat(cut.getNewTestResultCountByResultType(null).entrySet(), is(empty()));

		// 1a. Works for 1 case
		assertThat(cut.getNewTestResultCountByResultType(Collections.singletonList(1001L)).entrySet(), is(empty()));

		// 1b. Works for 2 cases
		assertThat(cut.getNewTestResultCountByResultType(Arrays.asList(1L, 2L)).entrySet(), is(empty()));

		// 1c. Works for 3 cases
		assertThat(cut.getNewTestResultCountByResultType(Arrays.asList(1L, 2L, 1001L)).entrySet(), is(empty()));

		// 2a. Works for 1_000 cases
		assertThat(cut.getNewTestResultCountByResultType(LongStream.rangeClosed(1, 1_000).boxed().collect(Collectors.toList())).entrySet(), is(empty()));

		// 2b. Works for 100_000 cases
		assertThat(cut.getNewTestResultCountByResultType(LongStream.rangeClosed(1, 100_000).boxed().collect(Collectors.toList())).entrySet(), is(empty()));
	}
}
