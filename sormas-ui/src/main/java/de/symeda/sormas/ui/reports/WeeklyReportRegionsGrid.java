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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
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
			
			String css;
			if (WeeklyReportRegionSummaryDto.OFFICERS.equals(cell.getPropertyId())
					|| WeeklyReportRegionSummaryDto.OFFICER_REPORTS.equals(cell.getPropertyId())
					|| WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE.equals(cell.getPropertyId())
					|| WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS.equals(cell.getPropertyId())) {
				css = CssStyles.GRID_CELL_ODD;
			} else {
				css = "";
			}
				
			if (WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE.equals(cell.getPropertyId())
					|| WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE.equals(cell.getPropertyId())) {
				Integer reportPercentage = (Integer) cell.getProperty().getValue();
				if (reportPercentage >= 90) {
					css += " " + CssStyles.GRID_CELL_PRIORITY_LOW;
				} else if (reportPercentage >= 60) {
					css += " " + CssStyles.GRID_CELL_PRIORITY_NORMAL;
				} else {
					css += " " + CssStyles.GRID_CELL_PRIORITY_HIGH;
				}
			}
			return css;
		}
	}

	public WeeklyReportRegionsGrid() {
		setSizeFull();

		BeanItemContainer<WeeklyReportRegionSummaryDto> container = new BeanItemContainer<WeeklyReportRegionSummaryDto>(
				WeeklyReportRegionSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		VaadinUiUtil.addIconColumn(generatedContainer, VIEW_DETAILS_BTN_ID, VaadinIcons.EYE);
		setContainerDataSource(generatedContainer);

		setColumns(VIEW_DETAILS_BTN_ID, 
				WeeklyReportRegionSummaryDto.REGION,
				WeeklyReportRegionSummaryDto.OFFICERS,
				WeeklyReportRegionSummaryDto.OFFICER_REPORTS, 
				WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE, 
				WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANTS,
				WeeklyReportRegionSummaryDto.INFORMANT_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE,
				WeeklyReportRegionSummaryDto.INFORMANT_ZERO_REPORTS);

		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixCaption(WeeklyReportRegionSummaryDto.I18N_PREFIX,
						column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}
		
		HeaderRow headerRow = getHeaderRow(0);
		headerRow.getCell(WeeklyReportRegionSummaryDto.OFFICERS).setStyleName(CssStyles.GRID_CELL_ODD);
		headerRow.getCell(WeeklyReportRegionSummaryDto.OFFICER_REPORTS).setStyleName(CssStyles.GRID_CELL_ODD);
		headerRow.getCell(WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE).setStyleName(CssStyles.GRID_CELL_ODD);
		headerRow.getCell(WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS).setStyleName(CssStyles.GRID_CELL_ODD);

		HeaderRow preHeaderRow = prependHeaderRow();
		HeaderCell preHeaderCell = preHeaderRow.join(
				WeeklyReportRegionSummaryDto.OFFICERS, 
				WeeklyReportRegionSummaryDto.OFFICER_REPORTS,
				WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE,
				WeeklyReportRegionSummaryDto.OFFICER_ZERO_REPORTS);
		preHeaderCell.setHtml(I18nProperties.getCaption(Captions.weeklyReportRegionOfficers));
		preHeaderCell.setStyleName(CssStyles.GRID_CELL_ODD);		
		
		preHeaderRow.join(
				WeeklyReportRegionSummaryDto.INFORMANTS, 
				WeeklyReportRegionSummaryDto.INFORMANT_REPORTS,
				WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE,
				WeeklyReportRegionSummaryDto.INFORMANT_ZERO_REPORTS)
			.setHtml(I18nProperties.getCaption(Captions.weeklyReportRegionInformants));

		getColumn(VIEW_DETAILS_BTN_ID).setRenderer(new HtmlRenderer());
		getColumn(VIEW_DETAILS_BTN_ID).setWidth(60);

		getColumn(WeeklyReportRegionSummaryDto.OFFICER_REPORT_PERCENTAGE).setRenderer(new PercentageRenderer());
		getColumn(WeeklyReportRegionSummaryDto.INFORMANT_REPORT_PERCENTAGE).setRenderer(new PercentageRenderer());

		setCellStyleGenerator(new WeeklyReportGridCellStyleGenerator());

		setSelectionMode(SelectionMode.NONE);
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
			layout.setSizeUndefined();
			layout.setMargin(true);
			Window window = VaadinUiUtil.showPopupWindow(layout);

			WeeklyReportOfficersGrid grid = new WeeklyReportOfficersGrid();
			grid.reload(summaryDto.getRegion(), year, week);
			grid.setWidth(1600, Unit.PIXELS);
			grid.setHeightMode(HeightMode.ROW);
			grid.setHeightUndefined();
			layout.addComponent(grid);
			window.setCaption(String.format(I18nProperties.getCaption(Captions.weeklyReportsInDistrict), summaryDto.getRegion().toString())
					+ " - " + I18nProperties.getString(Strings.epiWeek) + " " + week + "/" + year);
		}
	}
}
