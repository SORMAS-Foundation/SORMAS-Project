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
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EditPermissionType;
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
import de.symeda.sormas.api.vaccination.VaccinationAssociationType;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.ContactListComponent;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.externalmessage.ExternalMessagesView;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
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

//	public static final String HTML_LAYOUT = LayoutUtil.fluidRow(
//		LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, IMMUNIZATION_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, VACCINATIONS_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, QUARANTINE_LOC),
//		LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SORMAS_TO_SORMAS_LOC));

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

		final EventParticipantReferenceDto eventParticipantRef = getReference();
		editComponent = ControllerProvider.getEventParticipantController().getEventParticipantDataEditComponent(eventParticipantRef.getUuid());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(
			editComponent,
			SAMPLES_LOC,
			CONTACTS_LOC,
			IMMUNIZATION_LOC,
			VACCINATIONS_LOC,
			QUARANTINE_LOC,
			SORMAS_TO_SORMAS_LOC);

		container.addComponent(layout);

		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		SampleCriteria sampleCriteria = new SampleCriteria().eventParticipant(eventParticipantRef);
		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			SampleListComponent sampleList = new SampleListComponent(
				sampleCriteria.eventParticipant(eventParticipantRef)
					.disease(event.getDisease())
					.sampleAssociationType(SampleAssociationType.EVENT_PARTICIPANT),
				this::showUnsavedChangesPopup);
			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesEventParticipant));
			layout.addSidePanelComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			VerticalLayout contactsLayout = new VerticalLayout();
			contactsLayout.setMargin(false);
			contactsLayout.setSpacing(false);

			ContactListComponent contactList = new ContactListComponent(eventParticipantRef, this::showUnsavedChangesPopup);
			contactList.addStyleName(CssStyles.SIDE_COMPONENT);
			contactsLayout.addComponent(contactList);

			layout.addSidePanelComponent(contactsLayout, CONTACTS_LOC);
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isSharingEventsEnabledForUser();
		if (sormasToSormasEnabled || eventParticipant.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(eventParticipant, sormasToSormasEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		VaccinationListCriteria vaccinationCriteria =
			new VaccinationListCriteria.Builder(eventParticipant.getPerson().toReference()).withDisease(event.getDisease()).build();
		QuarantineOrderDocumentsComponent.addComponentToLayout(
			layout.getSidePanelComponent(),
			eventParticipantRef,
			DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
			sampleCriteria,
			vaccinationCriteria);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)
			&& event.getDisease() != null) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				final ImmunizationListCriteria immunizationListCriteria =
					new ImmunizationListCriteria.Builder(eventParticipant.getPerson().toReference()).wihDisease(event.getDisease()).build();
				layout.addSidePanelComponent(
					new SideComponentLayout(new ImmunizationListComponent(immunizationListCriteria, this::showUnsavedChangesPopup)),
					IMMUNIZATION_LOC);
			} else {
				VaccinationListCriteria criteria = vaccinationCriteria.vaccinationAssociationType(VaccinationAssociationType.EVENT_PARTICIPANT)
					.eventParticipantReference(getReference())
					.region(eventParticipant.getRegion() != null ? eventParticipant.getRegion() : event.getEventLocation().getRegion())
					.district(eventParticipant.getDistrict() != null ? eventParticipant.getDistrict() : event.getEventLocation().getDistrict());
				layout.addSidePanelComponent(
					new SideComponentLayout(new VaccinationListComponent(criteria, this::showUnsavedChangesPopup)),
					VACCINATIONS_LOC);
			}
		}

		EditPermissionType eventParticipantEditAllowed =
			FacadeProvider.getEventParticipantFacade().isEditAllowed(eventParticipantRef.getUuid());

		if (eventParticipantEditAllowed.equals(EditPermissionType.ARCHIVING_STATUS_ONLY)) {
			layout.disable(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
		} else if (eventParticipantEditAllowed.equals(EditPermissionType.REFUSED)) {
			layout.disable();
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

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EXTERNAL_MESSAGES)
			&& UserProvider.getCurrent().hasUserRight(UserRight.EXTERNAL_MESSAGE_VIEW)
			&& FacadeProvider.getExternalMessageFacade().existsExternalMessageForEntity(getReference())) {
			menu.addView(ExternalMessagesView.VIEW_NAME, I18nProperties.getCaption(Captions.externalMessagesList));
		}

		menu.addView(EventParticipantDataView.VIEW_NAME, I18nProperties.getCaption(EventParticipantDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getEventParticipantController().getEventParticipantViewTitleLayout(eventParticipantDto));
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);

		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());
		if (eventParticipant.isDeleted()) {
			newComponent.setEnabled(false);
		}

	}
}
