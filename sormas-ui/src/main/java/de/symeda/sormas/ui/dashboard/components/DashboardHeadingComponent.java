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

package de.symeda.sormas.ui.dashboard.components;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class DashboardHeadingComponent extends HorizontalLayout {

	private final Label totalLabel;

	public DashboardHeadingComponent(String titleCaption, String infoIconText) {
		setSpacing(false);

		// count
		totalLabel = new Label();
		CssStyles.style(
			totalLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		addComponent(totalLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(titleCaption));
		CssStyles.style(titleLabel, "heading-title-label", CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		addComponent(titleLabel);

		if (StringUtils.isNotBlank(infoIconText)) {
			Label infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			CssStyles.style(infoIcon, CssStyles.LABEL_LARGE, CssStyles.LABEL_SECONDARY, "statistics-info-label", CssStyles.HSPACE_LEFT_4);
			infoIcon.setDescription(infoIconText, ContentMode.HTML);
			addComponent(infoIcon);
		}
	}

	public void updateTotalLabel(String value) {
		totalLabel.setValue(value);
	}

	public void setTotalLabelDescription(String description) {
		totalLabel.setDescription(I18nProperties.getDescription(description));
	}
}
