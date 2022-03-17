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

public class SampleCountsTileViewLayout extends CssLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private DashboardDataProvider dashboardDataProvider;

	public SampleCountsTileViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
	}

	@Override
	protected String getCss(Component c) {
		c.setId("sample-card-wrapper");
		return "display: flex !important; flex-wrap: wrap !important; flex-direction: column; white-space: normal !important";
	}

	public void refresh() {
		Map<SampleCountType, Long> sampleCount = dashboardDataProvider.getSampleCount();
		Map<SampleCountType, Long> previousSampleCount = dashboardDataProvider.getPreviousSampleCounts();
		this.removeAllComponents();

		SampleCountType[] totalCol = {
			SampleCountType.COLLECTED };

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
			SampleCountType.RECEIVED,
			SampleCountType.NOT_RECEIVED };

		SampleCountType[] recievedCol = {
			SampleCountType.RECEIVED,
			SampleCountType.NOT_SHIPED };

		HorizontalLayout totalHorizontalLayout = new HorizontalLayout();
		totalHorizontalLayout.setWidth(500, Unit.PIXELS);
		totalHorizontalLayout.addComponent(createCountRow(totalCol, sampleCount, previousSampleCount, Captions.sampleSamples));
		addComponent(totalHorizontalLayout);

		HorizontalLayout conditionHorizontalLayout = new HorizontalLayout();
		conditionHorizontalLayout.addComponent(createCountRow(conditionCol, sampleCount, previousSampleCount, Captions.Sample_specimenCondition));
		addComponent(conditionHorizontalLayout);

		HorizontalLayout sampleTestResultHorizontalLayout = new HorizontalLayout();
		sampleTestResultHorizontalLayout.setWidth(100, Unit.PERCENTAGE);
		sampleTestResultHorizontalLayout.addComponent(createCountRow(resultTypeCol, sampleCount, previousSampleCount, Captions.Sample_testResult));
		addComponent(sampleTestResultHorizontalLayout);

		HorizontalLayout shipmentHorizontalLayout = new HorizontalLayout();
		shipmentHorizontalLayout.setWidth(100, Unit.PERCENTAGE);
		shipmentHorizontalLayout.addComponent(createCountRow(shipmentCol, sampleCount, previousSampleCount, Captions.sampleShipmentStatus));
		addComponent(shipmentHorizontalLayout);
	}

	private VerticalLayout createCountRow(
		SampleCountType[] cTypes,
		Map<SampleCountType, Long> sampleCount,
		Map<SampleCountType, Long> previousSampleCount,
		String label) {
		VerticalLayout verticalLayout = new VerticalLayout();

		Label title = new Label(I18nProperties.getCaption(label));
		CssStyles.style(title, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		verticalLayout.addComponent(title);

		HorizontalLayout countColorHorizontalLayout = new HorizontalLayout();
		for (SampleCountType type : cTypes) {
			SampleCountTileComponent tile = new SampleCountTileComponent(type, sampleCount.get(type), previousSampleCount.get(type));
			tile.setWidth(230, Unit.PIXELS);
			countColorHorizontalLayout.addComponent(tile);
		}
		verticalLayout.addComponent(countColorHorizontalLayout);
		return verticalLayout;
	}

}
