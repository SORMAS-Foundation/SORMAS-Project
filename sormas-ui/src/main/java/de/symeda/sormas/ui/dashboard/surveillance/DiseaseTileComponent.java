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
package de.symeda.sormas.ui.dashboard.surveillance;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseTileComponent extends VerticalLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	public DiseaseTileComponent(DiseaseBurdenDto diseaseBurden) {
		setMargin(false);
		setSpacing(false);

		addTopLayout(
			diseaseBurden.getDisease(),
			diseaseBurden.getCaseCount(),
			diseaseBurden.getPreviousCaseCount(),
			diseaseBurden.getOutbreakDistrictCount() > 0);
		addStatsLayout(diseaseBurden.getCaseDeathCount(), diseaseBurden.getEventCount(), diseaseBurden.getLastReportedDistrictName());
	}

	private void addTopLayout(Disease disease, Long casesCount, Long previousCasesCount, boolean isOutbreak) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.getDiseaseColor(disease));
		layout.setHeight(75, Unit.PIXELS);
		layout.setWidth(100, Unit.PERCENTAGE);

		VerticalLayout nameAndOutbreakLayout = new VerticalLayout();
		nameAndOutbreakLayout.setMargin(false);
		nameAndOutbreakLayout.setSpacing(false);
		nameAndOutbreakLayout.setHeight(100, Unit.PERCENTAGE);
		nameAndOutbreakLayout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setMargin(false);
		nameLayout.setSpacing(false);
		nameLayout.setWidth(100, Unit.PERCENTAGE);
		nameLayout.setHeight(100, Unit.PERCENTAGE);
		Label nameLabel = new Label(disease.toShortString());
		CssStyles.style(
			nameLabel,
			CssStyles.LABEL_WHITE,
			nameLabel.getValue().length() > 12 ? CssStyles.LABEL_LARGE : CssStyles.LABEL_XLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.ALIGN_CENTER,
			CssStyles.LABEL_UPPERCASE);
		nameLayout.addComponent(nameLabel);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
		nameAndOutbreakLayout.addComponent(nameLayout);
		nameAndOutbreakLayout.setExpandRatio(nameLayout, 1);

		if (isOutbreak) {
			HorizontalLayout outbreakLayout = new HorizontalLayout();
			outbreakLayout.setMargin(false);
			outbreakLayout.setSpacing(false);
			CssStyles.style(outbreakLayout, CssStyles.BACKGROUND_CRITICAL);
			outbreakLayout.setWidth(100, Unit.PERCENTAGE);
			outbreakLayout.setHeight(30, Unit.PIXELS);
			Label outbreakLabel = new Label(I18nProperties.getCaption(Captions.dashboardOutbreak));
			CssStyles.style(outbreakLabel, CssStyles.LABEL_WHITE, CssStyles.ALIGN_CENTER, CssStyles.LABEL_UPPERCASE);
			outbreakLayout.addComponent(outbreakLabel);
			outbreakLayout.setComponentAlignment(outbreakLabel, Alignment.MIDDLE_CENTER);
			nameAndOutbreakLayout.addComponent(outbreakLayout);
		}

		layout.addComponent(nameAndOutbreakLayout);
		layout.setExpandRatio(nameAndOutbreakLayout, 1);

		VerticalLayout countLayout = new VerticalLayout();
		countLayout.setMargin(false);
		countLayout.setSpacing(false);
		CssStyles.style(countLayout, CssStyles.getDiseaseColor(disease), CssStyles.BACKGROUND_DARKER);
		countLayout.setHeight(100, Unit.PERCENTAGE);
		countLayout.setWidth(100, Unit.PERCENTAGE);

		Label countLabel = new Label(casesCount.toString());
		CssStyles
			.style(countLabel, CssStyles.LABEL_WHITE, CssStyles.LABEL_BOLD, CssStyles.LABEL_XXXLARGE, CssStyles.ALIGN_CENTER, CssStyles.VSPACE_TOP_4);
		countLayout.addComponent(countLabel);
		countLayout.setComponentAlignment(countLabel, Alignment.BOTTOM_CENTER);

		HorizontalLayout comparisonLayout = new HorizontalLayout();
		{
			comparisonLayout.setMargin(false);
			comparisonLayout.setSpacing(false);

			Label growthLabel = new Label("", ContentMode.HTML);
			String chevronType = "";
			if (previousCasesCount < casesCount) {
				chevronType = VaadinIcons.CHEVRON_UP.getHtml();
			} else if (previousCasesCount > casesCount) {
				chevronType = VaadinIcons.CHEVRON_DOWN.getHtml();
			} else {
				chevronType = VaadinIcons.CHEVRON_RIGHT.getHtml();
			}
			growthLabel.setValue(
				"<div class=\"v-label v-widget " + CssStyles.LABEL_WHITE + " v-label-" + CssStyles.LABEL_WHITE
					+ " align-center v-label-align-center bold v-label-bold v-has-width\" " + "	  style=\"margin-top: 3px;\">"
					+ "		<span class=\"v-icon\" style=\"font-family: VaadinIcons;\">" + chevronType + "		</span>" + "</div>");

			comparisonLayout.addComponent(growthLabel);

			Label previousCountLabel = new Label(previousCasesCount.toString());
			CssStyles.style(previousCountLabel, CssStyles.LABEL_WHITE, CssStyles.LABEL_BOLD, CssStyles.LABEL_XLARGE, CssStyles.HSPACE_LEFT_4);
			comparisonLayout.addComponent(previousCountLabel);
			comparisonLayout.setComponentAlignment(growthLabel, Alignment.MIDDLE_CENTER);
			comparisonLayout.setComponentAlignment(previousCountLabel, Alignment.MIDDLE_CENTER);
		}
		countLayout.addComponent(comparisonLayout);
		countLayout.setComponentAlignment(comparisonLayout, Alignment.MIDDLE_CENTER);

		countLayout.setExpandRatio(countLabel, 0.4f);
		countLayout.setExpandRatio(comparisonLayout, 0.6f);

		layout.addComponent(countLayout);
		layout.setExpandRatio(countLayout, 0.65f);

		addComponent(layout);
	}

	private void addStatsLayout(Long fatalities, Long events, String district) {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.BACKGROUND_HIGHLIGHT);

		HorizontalLayout statsItem = createStatsItem(
			I18nProperties.getCaption(Captions.dashboardLastReport) + ": ",
			district.length() == 0 ? I18nProperties.getString(Strings.none) : district,
			false,
			true);
		CssStyles.style(statsItem, CssStyles.VSPACE_TOP_4);
		layout.addComponent(statsItem);
		layout.addComponent(createStatsItem(I18nProperties.getCaption(Captions.dashboardFatalities), fatalities.toString(), fatalities > 0, false));
		statsItem = createStatsItem(I18nProperties.getCaption(Captions.DiseaseBurden_eventCount), events.toString(), false, false);
		CssStyles.style(statsItem, CssStyles.VSPACE_4);
		layout.addComponent(statsItem);

		addComponent(layout);
	}

	private HorizontalLayout createStatsItem(String label, String value, boolean isCritical, boolean singleColumn) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		Label nameLabel = new Label(label);
		CssStyles.style(nameLabel, CssStyles.LABEL_PRIMARY, isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.HSPACE_LEFT_3);
		layout.addComponent(nameLabel);
		if (!singleColumn) {
			layout.setExpandRatio(nameLabel, 1);
		}

		Label valueLabel = new Label(value);
		CssStyles.style(
			valueLabel,
			CssStyles.LABEL_PRIMARY,
			isCritical ? CssStyles.LABEL_CRITICAL : "",
			singleColumn ? CssStyles.HSPACE_LEFT_5 : CssStyles.ALIGN_CENTER);
		layout.addComponent(valueLabel);
		layout.setExpandRatio(valueLabel, singleColumn ? 1f : 0.65f);

		return layout;
	}
}
