/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.sample.components;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class SampleCountTilesComponent<T extends Enum<?>> extends VerticalLayout {

	private final Class<T> groupType;

	private final Label title;
	private final HorizontalLayout countsLayout;
	private String[] titleStyle = new String[] {
		CssStyles.H3 };

	public SampleCountTilesComponent(Class<T> groupType, String titleCaption) {
		this.groupType = groupType;

		setMargin(new MarginInfo(false, true));
		setSpacing(false);
		setWidthFull();

		title = new Label(I18nProperties.getCaption(titleCaption));
		title.addStyleNames(titleStyle);
		addComponent(title);

		countsLayout = new HorizontalLayout();
		countsLayout.setWidthFull();
		addComponent(countsLayout);
	}

	public void update(Map<T, Long> counts) {
		countsLayout.removeAllComponents();

		Long total = counts.values().stream().reduce(0L, Long::sum);
		for (T group : groupType.getEnumConstants()) {
			Long count = counts.getOrDefault(group, 0L);
			BigDecimal percentage = new BigDecimal(count / total).multiply(new BigDecimal(100));
			percentage = percentage.setScale(0, RoundingMode.HALF_UP);

			TileComponent<T> tile = new TileComponent<>(group, count, percentage);
			tile.setWidthFull();

			countsLayout.addComponent(tile);
			countsLayout.setExpandRatio(tile, 1);
		}
	}

	public void setTitleStyleNames(String... styleNames) {
		title.removeStyleNames(titleStyle);
		this.titleStyle = styleNames;
		title.addStyleNames(styleNames);
	}

	private static class TileComponent<T> extends VerticalLayout {

		TileComponent(T group, Long count, BigDecimal percentage) {
			setSpacing(false);
			setMargin(false);

			Label countLabel = new Label(count.toString());
			countLabel
				.addStyleNames(CssStyles.LABEL_WHITE, CssStyles.LABEL_BOLD, CssStyles.LABEL_XLARGE, CssStyles.ALIGN_CENTER, CssStyles.VSPACE_TOP_4);

			Label percentageLabel = new Label(percentage.toString() + "%");
			percentageLabel.addStyleNames(CssStyles.LABEL_WHITE, CssStyles.ALIGN_CENTER, CssStyles.VSPACE_4);

			VerticalLayout numbersLayout = new VerticalLayout();
			numbersLayout.setWidthFull();
			numbersLayout.setMargin(false);
			numbersLayout.setSpacing(false);
			numbersLayout.addStyleNames("background-disease-afp");
			numbersLayout.addComponent(countLabel);
			numbersLayout.setComponentAlignment(countLabel, Alignment.TOP_CENTER);
			numbersLayout.addComponent(percentageLabel);
			numbersLayout.setComponentAlignment(percentageLabel, Alignment.MIDDLE_CENTER);

			addComponent(numbersLayout);

			Label groupLabel = new Label(String.valueOf(group));
			groupLabel.addStyleNames(
				CssStyles.LABEL_WHITE,
				CssStyles.LABEL_BOLD,
				CssStyles.LABEL_LARGE,
				CssStyles.VSPACE_TOP_4,
				CssStyles.VSPACE_4,
				CssStyles.HSPACE_LEFT_4,
				CssStyles.HSPACE_RIGHT_4);

			VerticalLayout groupLabelLayout = new VerticalLayout();
			groupLabelLayout.setMargin(false);
			groupLabelLayout.setSpacing(false);
			groupLabelLayout.addStyleNames("background-disease-afp", CssStyles.BACKGROUND_DARKER);
			groupLabelLayout.addComponent(groupLabel);
			groupLabelLayout.setComponentAlignment(groupLabel, Alignment.TOP_CENTER);

			addComponent(groupLabelLayout);
		}
	}
}
