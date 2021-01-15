package de.symeda.sormas.api.statistics;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.utils.EpiWeek;

public class StatisticsHelperTest {

	@Test
	public void testBuildGroupingKey() {

		EpiWeek epiWeek = (EpiWeek) StatisticsHelper
			.buildGroupingKey(1803, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR, null, null, null, null);
		assertThat(epiWeek.getYear(), equalTo(18));
		assertThat(epiWeek.getWeek(), equalTo(3));

		MonthOfYear monthOfYear = (MonthOfYear) StatisticsHelper
			.buildGroupingKey(1811, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.MONTH_OF_YEAR, null, null, null, null);
		assertThat(monthOfYear.getYear().getValue(), equalTo(18));
		assertThat(monthOfYear.getMonth(), equalTo(Month.NOVEMBER));

		QuarterOfYear quarterOfYear = (QuarterOfYear) StatisticsHelper
			.buildGroupingKey(182, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.QUARTER_OF_YEAR, null, null, null, null);
		assertThat(quarterOfYear.getYear().getValue(), equalTo(18));
		assertThat(quarterOfYear.getQuarter().getValue(), equalTo(2));
	}
}
