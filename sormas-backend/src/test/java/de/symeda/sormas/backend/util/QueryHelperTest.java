package de.symeda.sormas.backend.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.person.Sex;

/**
 * @see QueryHelper
 * @author stefan.kock
 */
public class QueryHelperTest {

	@Test
	public void testAppendInFilterValues() {

		StringBuilder result;

		// 1. Define filtering on the first value type
		List<Object> parameters = new ArrayList<>();
		StringBuilder sexFilterBuilder = new StringBuilder();
		List<Sex> sexValues = Arrays.asList(Sex.FEMALE, Sex.UNKNOWN);

		result = QueryHelper.appendInFilterValues(sexFilterBuilder, parameters, sexValues, entry -> entry.name());
		assertThat(result.toString(), equalTo("(?1,?2)"));
		assertThat(sexFilterBuilder.toString(), equalTo(result.toString()));
		assertThat(parameters, contains(Sex.FEMALE.name(), Sex.UNKNOWN.name()));

		// 2. Define filtering on the second value type, parameters are appended
		StringBuilder ageGroupsFilterBuilder = new StringBuilder();
		List<AgeGroup> ageGroupValues = Arrays.asList(AgeGroup.AGE_0_4, AgeGroup.AGE_15_19, AgeGroup.AGE_5_9);

		result = QueryHelper.appendInFilterValues(ageGroupsFilterBuilder, parameters, ageGroupValues, entry -> entry.name());
		assertThat(result.toString(), equalTo("(?3,?4,?5)"));
		assertThat(ageGroupsFilterBuilder.toString(), equalTo(result.toString()));
		assertThat(
			parameters,
			contains(Sex.FEMALE.name(), Sex.UNKNOWN.name(), AgeGroup.AGE_0_4.name(), AgeGroup.AGE_15_19.name(), AgeGroup.AGE_5_9.name()));
	}
}
