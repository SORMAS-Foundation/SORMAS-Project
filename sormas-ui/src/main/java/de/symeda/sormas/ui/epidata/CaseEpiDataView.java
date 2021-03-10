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
package de.symeda.sormas.ui.epidata;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.contact.SourceContactListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;

import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
public class CaseEpiDataView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/epidata";

	private static final String LOC_EPI_DATA = "epiData";
	private static final String LOC_SOURCE_CONTACTS = "sourceContacts";

	private CommitDiscardWrapperComponent<EpiDataForm> epiDataComponent;

	public CaseEpiDataView(SormasUI ui) {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(@NotNull final SormasUI ui, String params) {

		setHeightUndefined();

		String htmlLayout =
			LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, LOC_EPI_DATA), LayoutUtil.fluidColumnLoc(4, 0, 6, 0, LOC_SOURCE_CONTACTS));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> epiDataComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		boolean sourceContactsVisible = ui.getUserProvider().hasUserRight(UserRight.CONTACT_VIEW);
		VerticalLayout sourceContactsLayout = new VerticalLayout();
		Consumer<Boolean> sourceContactsToggleCallback = (visible) -> {
			sourceContactsLayout.setVisible(visible != null && sourceContactsVisible ? visible : false);
		};

		epiDataComponent = ControllerProvider.getCaseController().getEpiDataComponent(ui, getCaseRef().getUuid(), sourceContactsToggleCallback);
		epiDataComponent.setMargin(false);
		epiDataComponent.setWidth(100, Unit.PERCENTAGE);
		epiDataComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		epiDataComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(epiDataComponent, LOC_EPI_DATA);

		if (sourceContactsVisible) {
			sourceContactsLayout.setMargin(false);
			sourceContactsLayout.setSpacing(false);

			final SourceContactListComponent sourceContactList = new SourceContactListComponent(ui, getCaseRef());
			sourceContactList.addStyleName(CssStyles.SIDE_COMPONENT);
			sourceContactsLayout.addComponent(sourceContactList);

			if (ui.getUserProvider().hasUserRight(UserRight.CONTACT_CREATE)) {
				sourceContactList.addStyleName(CssStyles.VSPACE_NONE);
				Label contactCreationDisclaimer = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoCreateNewContactDiscardsChanges),
					ContentMode.HTML);
				contactCreationDisclaimer.addStyleName(CssStyles.VSPACE_TOP_4);

				sourceContactsLayout.addComponent(contactCreationDisclaimer);
			}

			if (sourceContactList.getSize() > 0) {
				epiDataComponent.getWrappedComponent().disableContactWithSourceCaseKnownField();
			}

			epiDataComponent.getWrappedComponent().setGetSourceContactsCallback(sourceContactList::getEntries);
		}
		layout.addComponent(sourceContactsLayout, LOC_SOURCE_CONTACTS);

		setCaseEditPermission(container);
	}
}
