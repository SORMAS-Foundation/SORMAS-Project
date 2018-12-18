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
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportOfficerSummaryDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PercentageRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class WeeklyReportOfficersGrid extends Grid implements ItemClickListener {

	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";

	@SuppressWarnings("unused")
	private RegionReferenceDto region;
	private int week;
	private int year;

	private final class WeeklyReportGridCellStyleGenerator implements CellStyleGenerator {
		@Override
		public String getStyle(CellReference cell) {
			if (WeeklyReportOfficerSummaryDto.INFORMANT_REPORT_PERCENTAGE.equals(cell.getPropertyId())) {
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

	public WeeklyReportOfficersGrid() {
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);
		setCellStyleGenerator(new WeeklyReportGridCellStyleGenerator());

		BeanItemContainer<WeeklyReportOfficerSummaryDto> container = new BeanItemContainer<WeeklyReportOfficerSummaryDto>(
				WeeklyReportOfficerSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, VIEW_DETAILS_BTN_ID, FontAwesome.EYE);
		setContainerDataSource(generatedContainer);

		setColumns(VIEW_DETAILS_BTN_ID, 
				WeeklyReportOfficerSummaryDto.OFFICER, 
				WeeklyReportOfficerSummaryDto.DISTRICT,
				WeeklyReportOfficerSummaryDto.OFFICER_REPORT_DATE, 
				WeeklyReportOfficerSummaryDto.INFORMANTS,
				WeeklyReportOfficerSummaryDto.INFORMANT_CASE_REPORTS,
				WeeklyReportOfficerSummaryDto.INFORMANT_ZERO_REPORTS,
				WeeklyReportOfficerSummaryDto.INFORMANT_REPORT_PERCENTAGE);

		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(WeeklyReportOfficerSummaryDto.I18N_PREFIX,
						column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}

		HeaderRow preHeaderRow = prependHeaderRow();
		preHeaderRow.join(WeeklyReportOfficerSummaryDto.INFORMANTS, 
				WeeklyReportOfficerSummaryDto.INFORMANT_CASE_REPORTS,
				WeeklyReportOfficerSummaryDto.INFORMANT_ZERO_REPORTS, 
				WeeklyReportOfficerSummaryDto.INFORMANT_REPORT_PERCENTAGE)
			.setHtml(getColumn(WeeklyReportOfficerSummaryDto.INFORMANTS).getHeaderCaption());
//		getColumn(WeeklyReportOfficerSummaryDto.INFORMANTS).setHeaderCaption("#");

		getColumn(WeeklyReportOfficerSummaryDto.INFORMANT_REPORT_PERCENTAGE).setRenderer(new PercentageRenderer());

		getColumn(VIEW_DETAILS_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(VIEW_DETAILS_BTN_ID).setWidth(60);

		addItemClickListener(this);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportOfficerSummaryDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<WeeklyReportOfficerSummaryDto>) container.getWrappedContainer();
	}

	public void reload(RegionReferenceDto region, int year, int week) {

		this.region = region;
		this.week = week;
		this.year = year;

		getContainer().removeAllItems();
		EpiWeek epiWeek = new EpiWeek(year, week);

		List<WeeklyReportOfficerSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade()
				.getSummariesPerOfficer(region, epiWeek);
		summaryDtos.forEach(s -> getContainer().addItem(s));
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
			WeeklyReportOfficerSummaryDto summaryDto = (WeeklyReportOfficerSummaryDto) event.getItemId();
			VerticalLayout layout = new VerticalLayout();
			Window window = VaadinUiUtil.showPopupWindow(layout);
			layout.setMargin(true);

			WeeklyReportInformantsGrid grid = new WeeklyReportInformantsGrid(summaryDto.getOfficer(), new EpiWeek(year, week));
			grid.setWidth(960, Unit.PIXELS);
			grid.setHeightMode(HeightMode.ROW);
			grid.setHeightUndefined();
			layout.addComponent(grid);
			window.setCaption(
					"Weekly Reports in " + summaryDto.getDistrict().toString() + " - Epi Week " + week + "/" + year);
		}
	}
}
