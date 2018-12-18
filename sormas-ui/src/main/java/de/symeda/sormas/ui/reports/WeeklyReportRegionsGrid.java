/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.reports;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.report.WeeklyReportRegionSummaryDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PercentageRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class WeeklyReportRegionsGrid extends Grid implements ItemClickListener {

	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";

	private int week;
	private int year;

	private final class WeeklyReportGridCellStyleGenerator implements CellStyleGenerator {
		@Override
		public String getStyle(CellReference cell) {
			if (WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE.equals(cell.getPropertyId())
					|| WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE.equals(cell.getPropertyId())) {
				Integer reportPercentage = (Integer) cell.getProperty().getValue();
				if (reportPercentage >= 90) {
					return CssStyles.GRID_CELL_PRIORITY_LOW;
				} else if (reportPercentage >= 60) {
					return CssStyles.GRID_CELL_PRIORITY_NORMAL;
				} else {
					return CssStyles.GRID_CELL_PRIORITY_HIGH;
				}
			}
			return null;
		}
	}

	public WeeklyReportRegionsGrid() {
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);
		setCellStyleGenerator(new WeeklyReportGridCellStyleGenerator());

		BeanItemContainer<WeeklyReportRegionSummaryDto> container = new BeanItemContainer<WeeklyReportRegionSummaryDto>(
				WeeklyReportRegionSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, VIEW_DETAILS_BTN_ID, FontAwesome.EYE);
		setContainerDataSource(generatedContainer);

		setColumns(VIEW_DETAILS_BTN_ID, WeeklyReportRegionSummaryDto.REGION, WeeklyReportRegionSummaryDto.OFFICERS,
				WeeklyReportRegionSummaryDto.OFFICER_CASE_REPORTS, WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS,
				WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE, WeeklyReportRegionSummaryDto.INFORMANTS,
				WeeklyReportRegionSummaryDto.INFORMANT_CASE_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANT_ZERO_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE);

		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(WeeklyReportRegionSummaryDto.I18N_PREFIX,
						column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}

		HeaderRow preHeaderRow = prependHeaderRow();
		preHeaderRow.join(WeeklyReportRegionSummaryDto.OFFICERS, 
				WeeklyReportRegionSummaryDto.OFFICER_CASE_REPORTS,
				WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS, 
				WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE)
			.setHtml(getColumn(WeeklyReportRegionSummaryDto.OFFICERS).getHeaderCaption());
//		getColumn(WeeklyReportRegionSummaryDto.OFFICERS).setHeaderCaption("#");
		
		preHeaderRow.join(WeeklyReportRegionSummaryDto.INFORMANTS, 
				WeeklyReportRegionSummaryDto.INFORMANT_CASE_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANT_ZERO_REPORTS, 
				WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE)
			.setHtml(getColumn(WeeklyReportRegionSummaryDto.INFORMANTS).getHeaderCaption());
//		getColumn(WeeklyReportRegionSummaryDto.INFORMANTS).setHeaderCaption("#");

		getColumn(WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE).setRenderer(new PercentageRenderer());
		getColumn(WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE).setRenderer(new PercentageRenderer());

		getColumn(VIEW_DETAILS_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(VIEW_DETAILS_BTN_ID).setWidth(60);

		addItemClickListener(this);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportRegionSummaryDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<WeeklyReportRegionSummaryDto>) container.getWrappedContainer();
	}

	public void reload(int year, int week) {

		this.week = week;
		this.year = year;

		getContainer().removeAllItems();
		EpiWeek epiWeek = new EpiWeek(year, week);

		List<WeeklyReportRegionSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade()
				.getSummariesPerRegion(epiWeek);
		summaryDtos.forEach(s -> getContainer().addItem(s));
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
			WeeklyReportRegionSummaryDto summaryDto = (WeeklyReportRegionSummaryDto) event.getItemId();
			VerticalLayout layout = new VerticalLayout();
			Window window = VaadinUiUtil.showPopupWindow(layout);
			layout.setMargin(true);

			WeeklyReportOfficersGrid grid = new WeeklyReportOfficersGrid();
			grid.reload(summaryDto.getRegion(), year, week);
			grid.setWidth(1080, Unit.PIXELS);
			grid.setHeightMode(HeightMode.ROW);
			grid.setHeightUndefined();
			layout.addComponent(grid);
			window.setCaption(
					"Weekly Reports in " + summaryDto.getRegion().toString() + " - Epi Week " + week + "/" + year);
		}
	}
}
