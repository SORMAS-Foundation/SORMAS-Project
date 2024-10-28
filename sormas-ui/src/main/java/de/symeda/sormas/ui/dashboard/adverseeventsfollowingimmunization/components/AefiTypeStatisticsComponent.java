/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization.components;

import java.util.Map;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.components.DashboardHeadingComponent;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.surveillance.components.statistics.DiseaseSectionStatisticsComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiTypeStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement seriousCount;
	private final DashboardStatisticsCountElement nonSeriousCount;
	private boolean withPercentage;

	public AefiTypeStatisticsComponent(String titleCaption, String description, String subtitleCaption, boolean showInfoIcon) {
		super(titleCaption, description, "");

		setWidthUndefined();

		if (subtitleCaption != null) {
			Label subTitleLabel = new Label(I18nProperties.getCaption(subtitleCaption));
			CssStyles.style(subTitleLabel, CssStyles.H3, CssStyles.VSPACE_TOP_5);
			addComponent(subTitleLabel);
		}

		// Count layout
		seriousCount = new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.aefiDashboardSerious), CountElementStyle.CRITICAL);
		nonSeriousCount =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.aefiDashboardNonSerious), CountElementStyle.POSITIVE);

		buildCountLayout(seriousCount, nonSeriousCount);
	}

	public void update(Map<AefiType, Long> aefiTypeData) {
		if (aefiTypeData != null) {
			Long totalCount = null;
			Long seriousTotal = aefiTypeData.getOrDefault(AefiType.SERIOUS, 0L);
			Long nonSeriousTotal = aefiTypeData.getOrDefault(AefiType.NON_SERIOUS, 0L);

			//updateTotalLabel(((Long) aefiTypeData.values().stream().mapToLong(Long::longValue).sum()).toString());
			if (withPercentage) {
				totalCount = aefiTypeData.values().stream().reduce(0L, Long::sum);
				seriousCount.updateCountLabel(seriousTotal + " (" + calculatePercentage(totalCount, seriousTotal) + " %)");
				nonSeriousCount.updateCountLabel(nonSeriousTotal + " (" + calculatePercentage(totalCount, nonSeriousTotal) + " %)");
			} else {
				seriousCount.updateCountLabel(seriousTotal.toString());
				nonSeriousCount.updateCountLabel(nonSeriousTotal.toString());
			}
		}
	}

	public int calculatePercentage(Long totalCount, Long labResultCount) {
		return totalCount == 0 ? 0 : (int) ((labResultCount * 100.0f) / totalCount);
	}

	public void setWithPercentage(boolean withPercentage) {
		this.withPercentage = withPercentage;
	}

	public void setTitleStyleNamesOnTitleLabel(String... styleNames) {
		DashboardHeadingComponent dashboardHeadingComponent = this.getHeading();
		Label titleLabel = dashboardHeadingComponent.getTitleLabel();

		titleLabel.removeStyleNames(dashboardHeadingComponent.getTitleStyleNames());
		titleLabel.addStyleNames(styleNames);
	}

	public void setTitleStyleNamesOnTotalLabel(String... styleNames) {
		DashboardHeadingComponent dashboardHeadingComponent = this.getHeading();
		Label totalLabel = dashboardHeadingComponent.getTotalLabel();

		totalLabel.removeStyleNames(dashboardHeadingComponent.getTotalLabelStyleNames());
		totalLabel.addStyleNames(styleNames);
	}
}
