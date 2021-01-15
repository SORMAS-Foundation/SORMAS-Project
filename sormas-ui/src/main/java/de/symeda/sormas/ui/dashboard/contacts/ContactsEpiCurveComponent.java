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
package de.symeda.sormas.ui.dashboard.contacts;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.contacts.epicurve.EpiCurveBuilder;
import de.symeda.sormas.ui.dashboard.contacts.epicurve.EpiCurveBuilders;
import de.symeda.sormas.ui.dashboard.diagram.AbstractEpiCurveComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactsEpiCurveComponent extends AbstractEpiCurveComponent {

	private static final long serialVersionUID = 6582975657305031105L;

	private ContactsEpiCurveMode epiCurveContactsMode;

	public ContactsEpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected PopupButton createEpiCurveModeSelector() {
		if (epiCurveContactsMode == null) {
			epiCurveContactsMode = ContactsEpiCurveMode.FOLLOW_UP_STATUS;
			epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpStatusChart));
		}

		VerticalLayout groupingLayout = new VerticalLayout();
		groupingLayout.setMargin(true);
		groupingLayout.setSizeUndefined();

		PopupButton dataDropdown = ButtonHelper.createPopupButton(Captions.dashboardData, groupingLayout, CssStyles.BUTTON_SUBTLE);

		OptionGroup dataSelect = new OptionGroup();
		dataSelect.setWidth(100, Unit.PERCENTAGE);
		dataSelect.addItems((Object[]) ContactsEpiCurveMode.values());
		dataSelect.setValue(epiCurveContactsMode);
		dataSelect.select(epiCurveContactsMode);
		dataSelect.addValueChangeListener(e -> {
			epiCurveContactsMode = (ContactsEpiCurveMode) e.getProperty().getValue();
			switch (epiCurveContactsMode) {
			case FOLLOW_UP_STATUS:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpStatusChart));
				break;
			case CONTACT_CLASSIFICATION:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardContactClassificationChart));
				break;
			case FOLLOW_UP_UNTIL:
				epiCurveLabel.setValue(I18nProperties.getCaption(Captions.dashboardFollowUpUntilChart));
				break;
			}
			clearAndFillEpiCurveChart();
		});
		groupingLayout.addComponent(dataSelect);

		return dataDropdown;
	}

	@Override
	public void clearAndFillEpiCurveChart() {
		EpiCurveBuilder epiCurveBuilder = EpiCurveBuilders.getEpiCurveBuilder(epiCurveContactsMode, epiCurveGrouping);
		epiCurveChart.setHcjs(epiCurveBuilder.buildFrom(buildListOfFilteredDates(), dashboardDataProvider));
	}
}
