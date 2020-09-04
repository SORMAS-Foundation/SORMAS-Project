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
//	private static final String BURDEN_LOC = "burden";
//	private static final String DIFFERENCE_LOC = "difference";
//	private static final String EXTEND_BUTTONS_LOC = "extendButtons";

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
//		
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
			SampleCountType.NOT_SHIPED,
			SampleCountType.RECEIVED };
		SampleCountType[] recievedCol = {
			SampleCountType.RECEIVED,
			SampleCountType.NOT_SHIPED };

		HorizontalLayout totalHorizontalLayout = new HorizontalLayout();
		totalHorizontalLayout.setWidth(100, Unit.PERCENTAGE);

		totalHorizontalLayout.addComponent(createCountRow(totalCol, sampleCount, Captions.dashboardDiseaseDifference));
		totalHorizontalLayout.addComponent(createCountRow(conditionCol, sampleCount, Captions.Sample_specimenCondition));

		addComponent(totalHorizontalLayout);

		HorizontalLayout sampleTestResultHorizontalLayout = new HorizontalLayout();

		sampleTestResultHorizontalLayout.addComponent(createCountRow(resultTypeCol, sampleCount, Captions.Sample_testResult));
		addComponent(sampleTestResultHorizontalLayout);

		HorizontalLayout shipmentHorizontalLayout = new HorizontalLayout();

		shipmentHorizontalLayout.addComponent(createCountRow(shipmentCol, sampleCount, Captions.Sample_shipment));
//		shipmentHorizontalLayout.addComponent(createCountRow(recievedCol, sampleCount, Captions.Sample_shipemt_status));

		addComponent(shipmentHorizontalLayout);

//
//		layout.addComponent(diseaseTileViewLayout);
//		layout.setExpandRatio(diseaseTileViewLayout, 1);
//		for (SampleCountType type : SampleCountType.values()) {
//			if (sampleCount.get(type) != null) {
//				CountTileComponent tile = new CountTileComponent(type, sampleCount.get(type));
//				tile.setWidth(230, Unit.PIXELS);
//				addComponent(tile);
//			}
//		}
	}

	private VerticalLayout createCountRow(SampleCountType[] cTypes, Map<SampleCountType, Long> sampleCount, String label) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label title = new Label(I18nProperties.getCaption(label));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		verticalLayout.addComponent(title);

		HorizontalLayout countColorHorizontalLayout = new HorizontalLayout();
		for (SampleCountType type : cTypes) {
			CountTileComponent tile = new CountTileComponent(type, sampleCount.get(type));
			tile.setWidth(230, Unit.PIXELS);
			countColorHorizontalLayout.addComponent(tile);
		}
		verticalLayout.addComponent(countColorHorizontalLayout);
		return verticalLayout;
	}

}
