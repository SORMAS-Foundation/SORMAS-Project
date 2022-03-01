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
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent.QUARANTINE_LOC;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.ContactListComponent;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.labmessage.LabMessagesView;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

public class EventParticipantDataView extends AbstractDetailView<EventParticipantReferenceDto> {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = EventParticipantsView.VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String SAMPLES_LOC = "samples";
	public static final String CONTACTS_LOC = "contacts";
	public static final String IMMUNIZATION_LOC = "immunizations";
	public static final String VACCINATIONS_LOC = "vaccinations";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";

	public static final String HTML_LAYOUT = LayoutUtil.fluidRow(
		LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, IMMUNIZATION_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, VACCINATIONS_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, QUARANTINE_LOC),
		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SORMAS_TO_SORMAS_LOC));

	private CommitDiscardWrapperComponent<?> editComponent;

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
		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());

		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
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

		editComponent = ControllerProvider.getEventParticipantController().getEventParticipantDataEditComponent(eventParticipantRef.getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);

		layout.addComponent(editComponent, EDIT_LOC);

		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		SampleCriteria sampleCriteria = new SampleCriteria().eventParticipant(eventParticipantRef);
		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			SampleListComponent sampleList = new SampleListComponent(
				sampleCriteria.sampleAssociationType(SampleAssociationType.EVENT_PARTICIPANT),
				e -> showNavigationConfirmPopupIfDirty(
					() -> ControllerProvider.getSampleController().create(eventParticipantRef, event.getDisease(), SormasUI::refreshView)));

			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesEventParticipant));
			layout.addComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			VerticalLayout contactsLayout = new VerticalLayout();
			contactsLayout.setMargin(false);
			contactsLayout.setSpacing(false);

			ContactListComponent contactList = new ContactListComponent(eventParticipantRef);
			contactList.addStyleName(CssStyles.SIDE_COMPONENT);
			contactsLayout.addComponent(contactList);

			layout.addComponent(contactsLayout, CONTACTS_LOC);
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isSharingEventsEnabledForUser();
		if (sormasToSormasEnabled || eventParticipant.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(eventParticipant, sormasToSormasEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		VaccinationListCriteria vaccinationCriteria =
			new VaccinationListCriteria.Builder(eventParticipant.getPerson().toReference()).withDisease(event.getDisease()).build();
		QuarantineOrderDocumentsComponent.addComponentToLayout(
			layout,
			eventParticipantRef,
			DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
			sampleCriteria,
			vaccinationCriteria);

		boolean isEditAllowed = FacadeProvider.getEventParticipantFacade().isEventParticipantEditAllowed(eventParticipantRef.getUuid());
		if (!isEditAllowed) {
			container.setEnabled(false);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)
			&& event.getDisease() != null) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				final ImmunizationListCriteria immunizationListCriteria =
					new ImmunizationListCriteria.Builder(eventParticipant.getPerson().toReference()).wihDisease(event.getDisease()).build();
				layout.addComponent(new SideComponentLayout(new ImmunizationListComponent(immunizationListCriteria)), IMMUNIZATION_LOC);
			} else {
				VaccinationListCriteria criteria = vaccinationCriteria;
				layout.addComponent(
					new SideComponentLayout(
						new VaccinationListComponent(
							getReference(),
							criteria,
							eventParticipant.getRegion() != null ? eventParticipant.getRegion() : event.getEventLocation().getRegion(),
							eventParticipant.getDistrict() != null ? eventParticipant.getDistrict() : event.getEventLocation().getDistrict(),
							this)),
					VACCINATIONS_LOC);
			}
		}
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

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

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.LAB_MESSAGES)
			&& UserProvider.getCurrent().hasUserRight(UserRight.LAB_MESSAGES)
			&& FacadeProvider.getLabMessageFacade().existsLabMessageForEntity(getReference())) {
			menu.addView(LabMessagesView.VIEW_NAME, I18nProperties.getCaption(Captions.labMessageLabMessagesList));
		}

		menu.addView(EventParticipantDataView.VIEW_NAME, I18nProperties.getCaption(EventParticipantDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getEventParticipantController().getEventParticipantViewTitleLayout(eventParticipantDto));
	}
}
