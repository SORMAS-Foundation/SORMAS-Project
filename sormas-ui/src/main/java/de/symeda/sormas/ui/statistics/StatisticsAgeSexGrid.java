package de.symeda.sormas.ui.statistics;

import java.util.Date;

import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.StatisticsCase;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsAgeSexGrid extends Grid {

	private static final int AGE_LIMIT_LOW = 4;
	private static final int AGE_LIMIT_MIDDLE = 14;
	
	private final class StatisticsAgeSexGridCellStyleGenerator implements CellStyleGenerator {
		@Override
		public String getStyle(CellReference cell) {
			if (cell.getPropertyId().equals("Caption")) {
				return CssStyles.GRID_ROW_TITLE;
			}
			return null;
		}
	}
	
	public StatisticsAgeSexGrid() {
		super();
		setSelectionMode(SelectionMode.NONE);
		setCellStyleGenerator(new StatisticsAgeSexGridCellStyleGenerator());
		
		setColumns("Caption", "Male", "Female", "Unknown", "Total");
		getColumn("Caption").setHeaderCaption("");
		
		setCaption("Case Count by Age and Sex");
	}
	
	public void reload(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to) {
		getContainerDataSource().removeAllItems();
		
		int[] male = new int[4];
		int[] female = new int[4];
		int[] unknown = new int[4];
		for (StatisticsCase statisticsCase : FacadeProvider.getCaseFacade().getCasesForStatistics(regionRef, districtRef, disease, from, to, LoginHelper.getCurrentUserAsReference().getUuid())) {
			Sex sex = statisticsCase.getSex();
			Integer age = statisticsCase.getApproximateAge();
			if (sex == null) {
				if (age == null) {
					unknown[3]++;
				} else {
					if (age <= AGE_LIMIT_LOW)
						unknown[0]++;
					else if (age <= AGE_LIMIT_MIDDLE)
						unknown[1]++;
					else
						unknown[2]++;
				}
			} else {
				switch (sex) {
				case MALE:
					if (age == null) {
						male[3]++;
					} else {
						if (age <= AGE_LIMIT_LOW)
							male[0]++;
						else if (age <= AGE_LIMIT_MIDDLE)
							male[1]++;
						else
							male[2]++;
					}
					break;
				case FEMALE:
					if (age == null) {
						female[3]++;
					} else {
						if (age <= AGE_LIMIT_LOW)
							female[0]++;
						else if (age <= AGE_LIMIT_MIDDLE)
							female[1]++;
						else
							female[2]++;						
					}
					break;
				}
			}			
		}
		
		int totalMale = male[0] + male[1] + male[2] + male[3];
		int totalFemale = female[0] + female[1] + female[2] + female[3];
		int totalUnknown = unknown[0] + unknown[1] + unknown[2] + unknown[3];
		int total = totalMale + totalFemale + totalUnknown;
		int totalAgeLow = male[0] + female[0] + unknown[0];
		int totalAgeMiddle = male[1] + female[1] + unknown[1];
		int totalAgeHigh = male[2] + female[2] + unknown[2];
		int totalAgeUnknown = male[3] + female[3] + unknown[3];
		
		Object content[][] = { {"0-" + AGE_LIMIT_LOW, male[0], female[0], unknown[0], totalAgeLow},
				{(AGE_LIMIT_LOW + 1) + "-" + AGE_LIMIT_MIDDLE, male[1], female[1], unknown[1], totalAgeMiddle},
				{(AGE_LIMIT_MIDDLE + 1) + "+", male[2], female[2], unknown[2], totalAgeHigh},
				{"Unknown", male[3], female[3], unknown[3], totalAgeUnknown},
				{"Total", totalMale, totalFemale, totalUnknown, total} };
		
		for (Object[] row : content) {
			addRow(row[0], String.valueOf(row[1]), String.valueOf(row[2]), String.valueOf(row[3]), String.valueOf(row[4]));
		}
	}
	
}
