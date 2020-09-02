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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.dashboard.samples;

import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleCountType;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class CountsTileViewLayout extends CssLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;
	private static final String BURDEN_LOC = "burden";
	private static final String DIFFERENCE_LOC = "difference";
	private static final String EXTEND_BUTTONS_LOC = "extendButtons";

	public CountsTileViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
//		setTemplateContents(
//			LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(6, 0, 12, 0, BURDEN_LOC), LayoutUtil.fluidColumnLoc(6, 0, 12, 0, DIFFERENCE_LOC))
//				+ LayoutUtil.loc(EXTEND_BUTTONS_LOC));
	}

	@Override
	protected String getCss(Component c) {
		return "margin-left: 18px; margin-bottom: 18px; display: flex; flex-direction: column";
	}

	public void refresh() {
		Map<SampleCountType, Long> sampleCount = dashboardDataProvider.getSampleCount();
		this.removeAllComponents();
		row1.setWidth(100, Unit.PERCENTAGE);
//		layout.setMargin(false);

		SampleCountType[] totalCol = {
			SampleCountType.TOTAL };
		SampleCountType[] resultTypeCol = {
			SampleCountType.INDETERMINATE,
			SampleCountType.PENDING,
			SampleCountType.NEGATIVE,
			SampleCountType.POSITIVE };
		SampleCountType[] conditionCol = {
			SampleCountType.ADEQUATE,
			SampleCountType.INADEQUATE };
		SampleCountType[] shipmentCol = {
			SampleCountType.SHIPPED,
			SampleCountType.NOT_SHIPED };
		SampleCountType[] recievedCol = {
			SampleCountType.RECEIVED,
			SampleCountType.NOT_SHIPED };

		HorizontalLayout row1 = new HorizontalLayout();
		row1.addComponent(createCountRow(totalCol, sampleCount));
		row1.addComponent(createCountRow(conditionCol, sampleCount));
		addComponent(row1);

		HorizontalLayout row2 = new HorizontalLayout();
		row2.addComponent(createCountRow(resultTypeCol, sampleCount));
		addComponent(row2);

		HorizontalLayout row3 = new HorizontalLayout();
		row3.addComponent(createCountRow(shipmentCol, sampleCount));
		row3.addComponent(createCountRow(recievedCol, sampleCount));
		addComponent(row3);

//
//		layout.addComponent(diseaseTileViewLayout);
//		layout.setExpandRatio(diseaseTileViewLayout, 1);
		for (SampleCountType type : SampleCountType.values()) {
			if (sampleCount.get(type) != null) {
				CountTileComponent tile = new CountTileComponent(type, sampleCount.get(type));
				tile.setWidth(230, Unit.PIXELS);
				addComponent(tile);
			}
		}
	}

	private VerticalLayout createCountRow(SampleCountType[] cTypes, Map<SampleCountType, Long> sampleCount) {
		VerticalLayout vLayout = new VerticalLayout();

		Label title = new Label(I18nProperties.getCaption(Captions.dashboardDiseaseDifference));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		vLayout.addComponent(title);

		HorizontalLayout row1 = new HorizontalLayout();
		for (SampleCountType type : cTypes) {
			CountTileComponent tile = new CountTileComponent(type, sampleCount.get(type));
			tile.setWidth(230, Unit.PIXELS);
			row1.addComponent(tile);
		}
		vLayout.addComponent(row1);
		return vLayout;
	}

}
