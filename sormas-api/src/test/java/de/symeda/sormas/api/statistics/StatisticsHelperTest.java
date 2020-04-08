package de.symeda.sormas.api.statistics;

import org.junit.Test;

import de.symeda.sormas.api.statistics.StatisticsAttribute;
import de.symeda.sormas.api.statistics.StatisticsSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsHelper;

public class StatisticsHelperTest {

	@Test
	public void testBuildGroupingKey() throws Exception {
		StatisticsHelper.buildGroupingKey(1803, StatisticsAttributeEnum.TIME, StatisticsSubAttributeEnum.EPI_WEEK_OF_YEAR, null, null);
		StatisticsHelper.buildGroupingKey(1811, StatisticsAttributeEnum.TIME, StatisticsSubAttributeEnum.MONTH_OF_YEAR, null, null);
		StatisticsHelper.buildGroupingKey(182, StatisticsAttributeEnum.TIME, StatisticsSubAttributeEnum.QUARTER_OF_YEAR, null, null);
	}

}
