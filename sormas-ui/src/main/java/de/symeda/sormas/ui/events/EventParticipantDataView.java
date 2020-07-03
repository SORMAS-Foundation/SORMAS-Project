/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.events.EventParticipantsView.EVENTPARTICIPANTS;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.samples.SampleListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class EventParticipantDataView extends AbstractDetailView<EventParticipantReferenceDto> {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = EVENTPARTICIPANTS + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String SAMPLES_LOC = "samples";

	public static final String HTML_LAYOUT =
		LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC), LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC));

	public EventParticipantDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected EventParticipantReferenceDto getReferenceByUuid(String uuid) {
		final EventParticipantReferenceDto reference;
		if (FacadeProvider.getEventParticipantFacade().exists(uuid)) {
			reference = FacadeProvider.getEventParticipantFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return EventParticipantsView.VIEW_NAME;
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(HTML_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		final EventParticipantReferenceDto eventParticipantRef = getReference();

		CommitDiscardWrapperComponent<?> editComponent =
			ControllerProvider.getEventParticipantController().getEventParticipantDataEditComponent(eventParticipantRef.getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			VerticalLayout sampleLocLayout = new VerticalLayout();
			sampleLocLayout.setMargin(false);
			sampleLocLayout.setSpacing(false);

			SampleListComponent sampleList = new SampleListComponent(eventParticipantRef);
			sampleList.addStyleName(CssStyles.SIDE_COMPONENT);
			sampleLocLayout.addComponent(sampleList);

			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
				sampleLocLayout.addComponent(
					new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChanges),
						ContentMode.HTML));
			}

			layout.addComponent(sampleLocLayout, SAMPLES_LOC);
		}
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		EventParticipantDto eventParticipantDto = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());

		menu.removeAllViews();
		menu.addView(
			EventParticipantsView.VIEW_NAME,
			I18nProperties.getCaption(Captions.eventEventParticipants),
			eventParticipantDto.getEvent().getUuid(),
			true);

		infoLabel.setValue(I18nProperties.getCaption(Captions.EventParticipant));
		infoLabelSub.setValue(eventParticipantDto.getPerson().toString());
	}
}
