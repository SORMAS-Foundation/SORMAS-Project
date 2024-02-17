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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class AefiCountTilesComponent<T extends Enum<?>> extends VerticalLayout {

	private static final long serialVersionUID = 3342746874277667267L;
	private final Class<T> groupType;
	private final Function<T, String> tileBackgroundFactory;
	private boolean withPercentage;
	private String groupLabelStyle;

	private final Label title;
	private Label infoIcon;
	private final HorizontalLayout countsLayout;
	private String[] titleStyleNames = new String[] {
		CssStyles.H3 };

	public AefiCountTilesComponent(
		Class<T> groupType,
		String titleCaption,
		Function<T, String> tileBackgroundFactory,
		@Nullable String descriptionTag) {
		this.groupType = groupType;
		this.tileBackgroundFactory = tileBackgroundFactory;

		setMargin(false);
		setSpacing(false);
		setWidthFull();

		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setMargin(false);
		titleLayout.setSpacing(false);
		addComponent(titleLayout);

		title = new Label(I18nProperties.getCaption(titleCaption));
		title.addStyleNames(titleStyleNames);

		titleLayout.addComponent(title);

		if (StringUtils.isNotBlank(descriptionTag)) {
			infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			infoIcon.setDescription(I18nProperties.getDescription(descriptionTag));
			infoIcon.addStyleName(CssStyles.HSPACE_LEFT_4);
			infoIcon.addStyleNames(titleStyleNames);

			titleLayout.addComponent(infoIcon);
		}

		countsLayout = new HorizontalLayout();
		countsLayout.setWidthFull();
		addComponent(countsLayout);
	}

	public void update(Map<T, Long> counts) {
		countsLayout.removeAllComponents();

		Long total = null;
		if (withPercentage) {
			total = counts.values().stream().reduce(0L, Long::sum);
		}

		addTiles(counts, total);
	}

	private void addTiles(Map<T, Long> counts, @Nullable Long total) {
		for (T group : groupType.getEnumConstants()) {
			addTileForGroup(counts, group, total, String.valueOf(group));
		}

		if (counts.containsKey(null)) {
			addTileForGroup(counts, null, total, I18nProperties.getCaption(Captions.notSpecified));
		}
	}

	private void addTileForGroup(Map<T, Long> counts, T group, Long total, String groupCaption) {
		Long count = counts.getOrDefault(group, 0L);

		BigDecimal percentage = null;
		if (total != null) {
			percentage = total == 0
				? BigDecimal.ZERO
				: BigDecimal.valueOf(count).divide(BigDecimal.valueOf(total), MathContext.DECIMAL32).multiply(new BigDecimal(100));
			percentage = percentage.setScale(0, RoundingMode.HALF_UP);
		}

		TileComponent<T> tile = new TileComponent<>(groupCaption, count, percentage, groupLabelStyle, tileBackgroundFactory.apply(group));
		tile.setWidthFull();

		countsLayout.addComponent(tile);
		countsLayout.setExpandRatio(tile, 1);
	}

	public void setTitleStyleNames(String... styleNames) {
		title.removeStyleNames(titleStyleNames);
		title.addStyleNames(styleNames);

		if (infoIcon != null) {
			infoIcon.removeStyleNames(titleStyleNames);
			infoIcon.addStyleNames(styleNames);
		}

		this.titleStyleNames = styleNames;
	}

	public void setWithPercentage(boolean withPercentage) {
		this.withPercentage = withPercentage;
	}

	public void setGroupLabelStyle(String groupLabelStyle) {
		this.groupLabelStyle = groupLabelStyle;
	}

	private static class TileComponent<T> extends VerticalLayout {

		private static final long serialVersionUID = 5055236377479070515L;

		TileComponent(String group, Long count, @Nullable BigDecimal percentage, String groupLabelStyle, String backgroundStyle) {
			setSpacing(false);
			setMargin(false);

			VerticalLayout numbersLayout = new VerticalLayout();
			numbersLayout.setWidthFull();
			numbersLayout.setMargin(false);
			numbersLayout.setSpacing(false);
			numbersLayout.addStyleNames(backgroundStyle, CssStyles.ROUNDED_BORDER_TOP);
			addComponent(numbersLayout);

			Label countLabel = new Label(count.toString());
			countLabel
				.addStyleNames(CssStyles.LABEL_WHITE, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE, CssStyles.ALIGN_CENTER, CssStyles.VSPACE_TOP_4);

			numbersLayout.addComponent(countLabel);
			numbersLayout.setComponentAlignment(countLabel, Alignment.TOP_CENTER);

			if (percentage != null) {
				Label percentageLabel = new Label(percentage + "%");
				percentageLabel.addStyleNames(CssStyles.LABEL_WHITE, CssStyles.ALIGN_CENTER, CssStyles.VSPACE_4);

				numbersLayout.addComponent(percentageLabel);
				numbersLayout.setComponentAlignment(percentageLabel, Alignment.MIDDLE_CENTER);
			}

			Label groupLabel = new Label(group);
			groupLabel.addStyleNames(
				CssStyles.LABEL_WHITE,
				CssStyles.LABEL_BOLD,
				CssStyles.VSPACE_TOP_4,
				CssStyles.VSPACE_4,
				CssStyles.HSPACE_LEFT_4,
				CssStyles.HSPACE_RIGHT_4);

			if (groupLabelStyle != null) {
				groupLabel.addStyleName(groupLabelStyle);
			}

			VerticalLayout groupLabelLayout = new VerticalLayout();
			groupLabelLayout.setMargin(false);
			groupLabelLayout.setSpacing(false);
			groupLabelLayout.addStyleNames(backgroundStyle, CssStyles.BACKGROUND_DARKER, CssStyles.ROUNDED_BORDER_BOTTOM);
			groupLabelLayout.addComponent(groupLabel);
			groupLabelLayout.setComponentAlignment(groupLabel, Alignment.TOP_CENTER);

			addComponent(groupLabelLayout);
		}
	}
}
