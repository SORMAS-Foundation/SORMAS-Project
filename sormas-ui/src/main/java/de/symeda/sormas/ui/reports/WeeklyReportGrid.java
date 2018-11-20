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
import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class WeeklyReportGrid extends Grid implements ItemClickListener {

	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";

	private RegionReferenceDto region; // may be null
	private int week;
	private int year;
	
	private final class WeeklyReportGridCellStyleGenerator implements CellStyleGenerator {
		@Override
		public String getStyle(CellReference cell) {
			if (WeeklyReportSummaryDto.MISSING_REPORTS_PERCENTAGE.equals(cell.getPropertyId())) {
				Float missingReportsPercentage = (Float)cell.getProperty().getValue();
				if (missingReportsPercentage <= 10) {
					return CssStyles.GRID_CELL_PRIORITY_LOW;
				} else if (missingReportsPercentage <= 40) {
					return CssStyles.GRID_CELL_PRIORITY_NORMAL;
				} else {
					return CssStyles.GRID_CELL_PRIORITY_HIGH;
				}
			}
			return null;
		}
	}

	public WeeklyReportGrid() {
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);
		setCellStyleGenerator(new WeeklyReportGridCellStyleGenerator());
		
		BeanItemContainer<WeeklyReportSummaryDto> container = new BeanItemContainer<WeeklyReportSummaryDto>(WeeklyReportSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addIconColumn(generatedContainer, VIEW_DETAILS_BTN_ID, FontAwesome.EYE);
		setContainerDataSource(generatedContainer);
		
		setColumns(VIEW_DETAILS_BTN_ID, 
				WeeklyReportSummaryDto.REGION, WeeklyReportSummaryDto.DISTRICT, 
				WeeklyReportSummaryDto.FACILITIES, 
				WeeklyReportSummaryDto.REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.REPORTS, 
				WeeklyReportSummaryDto.ZERO_REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.ZERO_REPORTS, 
				WeeklyReportSummaryDto.MISSING_REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.MISSING_REPORTS
				);
		
		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
						WeeklyReportSummaryDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}

		HeaderRow preHeaderRow = prependHeaderRow();
		preHeaderRow.join(WeeklyReportSummaryDto.REPORTS_PERCENTAGE, WeeklyReportSummaryDto.REPORTS)
			.setHtml(getColumn(WeeklyReportSummaryDto.REPORTS).getHeaderCaption());
		getColumn(WeeklyReportSummaryDto.REPORTS).setHeaderCaption("#");
		preHeaderRow.join(WeeklyReportSummaryDto.ZERO_REPORTS_PERCENTAGE, WeeklyReportSummaryDto.ZERO_REPORTS)
			.setHtml(getColumn(WeeklyReportSummaryDto.ZERO_REPORTS).getHeaderCaption());
		getColumn(WeeklyReportSummaryDto.ZERO_REPORTS).setHeaderCaption("#");
		preHeaderRow.join(WeeklyReportSummaryDto.MISSING_REPORTS_PERCENTAGE, WeeklyReportSummaryDto.MISSING_REPORTS)
			.setHtml(getColumn(WeeklyReportSummaryDto.MISSING_REPORTS).getHeaderCaption());
		getColumn(WeeklyReportSummaryDto.MISSING_REPORTS).setHeaderCaption("#");

		preHeaderRow.getCell(WeeklyReportSummaryDto.FACILITIES)
			.setHtml(getColumn(WeeklyReportSummaryDto.FACILITIES).getHeaderCaption());
		getColumn(WeeklyReportSummaryDto.FACILITIES).setHeaderCaption("#");

        getColumn(VIEW_DETAILS_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(VIEW_DETAILS_BTN_ID).setWidth(60);
	
		addItemClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportSummaryDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<WeeklyReportSummaryDto>) container.getWrappedContainer();
	}
	
	public void reload(RegionReferenceDto region, int year, int week) {
		
		this.region = region;
		this.week = week;
		this.year = year;
		
		getContainer().removeAllItems();
		EpiWeek epiWeek = new EpiWeek(year, week);
		
		if (region == null) {
			List<WeeklyReportSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade().getSummariesPerRegion(epiWeek);
			summaryDtos.forEach(s -> getContainer().addItem(s));

			getColumn(WeeklyReportSummaryDto.DISTRICT).setHidden(true);
			getColumn(WeeklyReportSummaryDto.REGION).setHidden(false);
		} else {
			List<WeeklyReportSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade().getSummariesPerDistrict(region, epiWeek);
			summaryDtos.forEach(s -> getContainer().addItem(s));

			getColumn(WeeklyReportSummaryDto.DISTRICT).setHidden(false);
			getColumn(WeeklyReportSummaryDto.REGION).setHidden(true);
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
			WeeklyReportSummaryDto summaryDto = (WeeklyReportSummaryDto) event.getItemId();
			VerticalLayout layout = new VerticalLayout();
			Window window = VaadinUiUtil.showPopupWindow(layout);
			layout.setMargin(true);

			if (region != null) {
				WeeklyReportDetailsGrid grid = new WeeklyReportDetailsGrid(summaryDto.getDistrict(), new EpiWeek(year, week));
				grid.setWidth(960, Unit.PIXELS);
				grid.setHeightMode(HeightMode.ROW);
				grid.setHeightUndefined();
				layout.addComponent(grid);
				window.setCaption("Weekly Reports in " + summaryDto.getDistrict().toString() + " - Epi Week " + week + "/" + year);
			} else {
				WeeklyReportGrid grid = new WeeklyReportGrid();
				grid.reload(summaryDto.getRegion(), year, week);
				grid.setWidth(960, Unit.PIXELS);
				grid.setHeightMode(HeightMode.ROW);
				grid.setHeightUndefined();
				layout.addComponent(grid);
				window.setCaption("Weekly Reports in " + summaryDto.getRegion().toString() + " - Epi Week " + week + "/" + year);
			}
		}
	}
}
