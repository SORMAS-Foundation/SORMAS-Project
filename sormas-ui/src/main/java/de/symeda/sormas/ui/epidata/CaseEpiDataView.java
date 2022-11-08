/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.epidata;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.contact.SourceContactListComponent;
import de.symeda.sormas.ui.travelentry.travelentrylink.TravelEntryListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class CaseEpiDataView extends AbstractCaseView {

	private static final long serialVersionUID = -2234439339327723422L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/epidata";

	private static final String LOC_EPI_DATA = "epiData";
	private static final String LOC_SOURCE_CONTACTS = "sourceContacts";
	public static final String TRAVEL_ENTRIES_LOC = "travelEntries";

	private CommitDiscardWrapperComponent<EpiDataForm> epiDataComponent;

	public CaseEpiDataView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> epiDataComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		UserProvider currentUser = UserProvider.getCurrent();
		boolean sourceContactsVisible = currentUser != null && currentUser.hasUserRight(UserRight.CONTACT_VIEW);
		VerticalLayout sourceContactsLayout = new VerticalLayout();
		Consumer<Boolean> sourceContactsToggleCallback =
			(visible) -> sourceContactsLayout.setVisible(visible != null && sourceContactsVisible ? visible : false);

		epiDataComponent =
			ControllerProvider.getCaseController().getEpiDataComponent(getCaseRef().getUuid(), sourceContactsToggleCallback, isEditAllowed());

		LayoutWithSidePanel layout = new LayoutWithSidePanel(epiDataComponent, LOC_SOURCE_CONTACTS, TRAVEL_ENTRIES_LOC);
		container.addComponent(layout);

		if (sourceContactsVisible) {
			sourceContactsLayout.setMargin(false);
			sourceContactsLayout.setSpacing(false);

			final SourceContactListComponent sourceContactList = new SourceContactListComponent(getCaseRef(), this, isEditAllowed());
			sourceContactList.addStyleName(CssStyles.SIDE_COMPONENT);
			sourceContactsLayout.addComponent(sourceContactList);

			if (currentUser.hasUserRight(UserRight.CONTACT_CREATE)) {
				sourceContactList.addStyleName(CssStyles.VSPACE_NONE);
				Label contactCreationDisclaimer = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoCreateNewContactDiscardsChanges),
					ContentMode.HTML);
				contactCreationDisclaimer.addStyleName(CssStyles.VSPACE_TOP_4);

				sourceContactsLayout.addComponent(contactCreationDisclaimer);
			}

			epiDataComponent.getWrappedComponent().setGetSourceContactsCallback(sourceContactList::getEntries);
		}
		layout.addSidePanelComponent(sourceContactsLayout, LOC_SOURCE_CONTACTS);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TRAVEL_ENTRIES)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			TravelEntryListCriteria travelEntryListCriteria = new TravelEntryListCriteria.Builder().withCase(getCaseRef()).build();
			layout.addSidePanelComponent(
				new SideComponentLayout(new TravelEntryListComponent(travelEntryListCriteria, this::showUnsavedChangesPopup, isEditAllowed())),
				TRAVEL_ENTRIES_LOC);
		}

		setEditPermission(epiDataComponent, EpiDataDto.EXPOSURES, EpiDataDto.ACTIVITIES_AS_CASE);
	}
}
