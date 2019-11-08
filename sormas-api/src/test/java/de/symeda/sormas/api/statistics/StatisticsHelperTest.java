package de.symeda.sormas.api.statistics;

import org.junit.Test;

public class StatisticsHelperTest {

	@Test
	public void testBuildGroupingKey() throws Exception {
		StatisticsHelper.buildGroupingKey(1803, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR);
		StatisticsHelper.buildGroupingKey(1811, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.MONTH_OF_YEAR);
		StatisticsHelper.buildGroupingKey(182, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.QUARTER_OF_YEAR);
	}

}
