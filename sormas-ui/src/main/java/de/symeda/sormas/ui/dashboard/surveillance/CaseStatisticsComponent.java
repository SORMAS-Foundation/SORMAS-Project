/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.dashboard.surveillance;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardCssStyles;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseStatisticsComponent extends CustomLayout {

	private DashboardDataProvider dashboardDataProvider;

	private Label contactsConvertedToCase;
	private Label casesInQuarantineByDate;
	private Label casesPlacedInQuarantineByDate;

	protected VerticalLayout casesStatisticsLayout;

	public CaseStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;

		casesStatisticsLayout = createCasesStatisticsLayout();
		addComponent(casesStatisticsLayout);
	}

	private VerticalLayout createCasesStatisticsLayout() {

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addStyleName(DashboardCssStyles.HIGHLIGHTED_STATISTICS_COMPONENT);
		verticalLayout.setWidth(100, Unit.PERCENTAGE);
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);

		HorizontalLayout titleAndDateSelectionLayout = new HorizontalLayout();

		Label title = new Label(I18nProperties.getString(Strings.headingQuarantineForCases));
//		CssStyles.style(title, CssStyles.LABEL_XLARGE, CssStyles.LABEL_BOLD, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);

		CssStyles.style(title, CssStyles.LABEL_PRIMARY, CssStyles.LABEL_XLARGE, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		titleAndDateSelectionLayout.addComponent(title);
		verticalLayout.addComponent(titleAndDateSelectionLayout);

		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.HIGHLIGHTED_STATISTICS_COMPONENT);
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		layout.setMargin(false);
		layout.setSpacing(false);

		HorizontalLayout contactsConvertedToCasesLayout = createContactsConvertedToCasesLayout();
		layout.addComponent(contactsConvertedToCasesLayout);

		HorizontalLayout casesInQuarantineLayout = createCasesInQuarantineLayout();
		layout.addComponent(casesInQuarantineLayout);

		HorizontalLayout casesPlacedInQuarantineLayout = createCasesPlacedInQuarantineLayout();
		layout.addComponent(casesPlacedInQuarantineLayout);

		layout.setComponentAlignment(casesPlacedInQuarantineLayout, Alignment.MIDDLE_RIGHT);

		verticalLayout.addComponent(layout);

		return verticalLayout;
	}

	private HorizontalLayout createContactsConvertedToCasesLayout() {

		HorizontalLayout layout = new HorizontalLayout();

		Label captionInQuarantine = new Label(I18nProperties.getString(Strings.headingContactsConvertedToCase));
		CssStyles.style(captionInQuarantine, CssStyles.H3, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);
		layout.addComponent(captionInQuarantine);

		contactsConvertedToCase = new Label();
		CssStyles.style(
			contactsConvertedToCase,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_LARGE_ALT,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_TOP_NONE,
			CssStyles.HSPACE_RIGHT_3);
		layout.addComponent(contactsConvertedToCase);

		return layout;
	}

	private HorizontalLayout createCasesInQuarantineLayout() {

		HorizontalLayout layout = new HorizontalLayout();

		Label captionInQuarantine = new Label(I18nProperties.getString(Strings.headingCasesInQuarantine));
		CssStyles.style(captionInQuarantine, CssStyles.H3, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);
		layout.addComponent(captionInQuarantine);

		casesInQuarantineByDate = new Label();
		CssStyles.style(
			casesInQuarantineByDate,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_LARGE_ALT,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_5,
			CssStyles.HSPACE_RIGHT_3);
		layout.addComponent(casesInQuarantineByDate);

		return layout;
	}

	private HorizontalLayout createCasesPlacedInQuarantineLayout() {

		HorizontalLayout layout = new HorizontalLayout();

		Label captionPlacedInQuarantine = new Label(I18nProperties.getString(Strings.headingCasesPlacedInQuarantine));
		CssStyles.style(captionPlacedInQuarantine, CssStyles.H3, CssStyles.HSPACE_RIGHT_1, CssStyles.VSPACE_TOP_NONE);
		layout.addComponent(captionPlacedInQuarantine);

		casesPlacedInQuarantineByDate = new Label();
		CssStyles.style(
			casesPlacedInQuarantineByDate,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_LARGE_ALT,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_5,
			CssStyles.HSPACE_RIGHT_3);
		layout.addComponent(casesPlacedInQuarantineByDate);

		return layout;
	}

	private void updateCasesInQuarantineData() {

		contactsConvertedToCase.setValue(dashboardDataProvider.getContactsConvertedToCaseCount().toString());

		casesInQuarantineByDate.setValue(dashboardDataProvider.getCasesInQuarantineCount().toString());

		casesPlacedInQuarantineByDate.setValue(dashboardDataProvider.getCasesPlacedInQuarantineCount().toString());

	}

	public void refresh() {
		dashboardDataProvider.refreshData();

		updateCasesInQuarantineData();
	}

}
