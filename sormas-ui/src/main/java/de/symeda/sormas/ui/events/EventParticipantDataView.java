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

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationAssociationType;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.ContactListComponent;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.email.ExternalEmailSideComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

public class EventParticipantDataView extends AbstractEventParticipantView implements HasName {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String SAMPLES_LOC = "samples";
	public static final String CONTACTS_LOC = "contacts";
	public static final String IMMUNIZATION_LOC = "immunizations";
	public static final String VACCINATIONS_LOC = "vaccinations";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";
	public static final String EXTERNAL_EMAILS_LOC = "externalEmails";

	private CommitDiscardWrapperComponent<EventParticipantEditForm> editComponent;

	public EventParticipantDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());

		setHeightUndefined();

		final EventParticipantReferenceDto eventParticipantRef = getReference();
		final String uuid = eventParticipantRef.getUuid();
		editComponent = ControllerProvider.getEventParticipantController().getEventParticipantDataEditComponent(uuid);

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
			SORMAS_TO_SORMAS_LOC,
			EXTERNAL_EMAILS_LOC);

		container.addComponent(layout);

		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		EditPermissionType eventParticipantEditAllowed =
			FacadeProvider.getEventParticipantFacade().getEditPermissionType(eventParticipantRef.getUuid());
		boolean editAllowed = isEditAllowed();

		SampleCriteria sampleCriteria = new SampleCriteria().eventParticipant(eventParticipantRef);
		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			SampleListComponent sampleList = new SampleListComponent(
				sampleCriteria.eventParticipant(eventParticipantRef)
					.disease(event.getDisease())
					.sampleAssociationType(SampleAssociationType.EVENT_PARTICIPANT),
				this::showUnsavedChangesPopup,
				editAllowed,
				SampleAssociationType.EVENT_PARTICIPANT);
			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesEventParticipant));
			layout.addSidePanelComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			VerticalLayout contactsLayout = new VerticalLayout();
			contactsLayout.setMargin(false);
			contactsLayout.setSpacing(false);

			ContactListComponent contactList = new ContactListComponent(eventParticipantRef, this::showUnsavedChangesPopup, editAllowed);
			contactList.addStyleName(CssStyles.SIDE_COMPONENT);
			contactsLayout.addComponent(contactList);

			layout.addSidePanelComponent(contactsLayout, CONTACTS_LOC);
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isAnyFeatureConfigured(FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS);
		if (sormasToSormasEnabled || eventParticipant.getSormasToSormasOriginInfo() != null || eventParticipant.isOwnershipHandedOver()) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(eventParticipant);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		VaccinationCriteria vaccinationCriteria =
			new VaccinationCriteria.Builder(eventParticipant.getPerson().toReference()).withDisease(event.getDisease()).build();
		QuarantineOrderDocumentsComponent.addComponentToLayout(
			layout,
			RootEntityType.ROOT_EVENT_PARTICIPANT,
			eventParticipantRef,
			DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
			sampleCriteria,
			vaccinationCriteria);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)
			&& event.getDisease() != null) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				layout.addSidePanelComponent(
					new SideComponentLayout(
						new ImmunizationListComponent(
							() -> new ImmunizationListCriteria.Builder(eventParticipant.getPerson().toReference()).withDisease(event.getDisease())
								.build(),
							null,
							this::showUnsavedChangesPopup,
							editAllowed)),
					IMMUNIZATION_LOC);
			} else {
				layout.addSidePanelComponent(new SideComponentLayout(new VaccinationListComponent(() -> {
					EventParticipantDto refreshedEventParticipant =
						FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid());
					RegionReferenceDto region =
						refreshedEventParticipant.getRegion() != null ? refreshedEventParticipant.getRegion() : event.getEventLocation().getRegion();
					DistrictReferenceDto district = refreshedEventParticipant.getDistrict() != null
						? refreshedEventParticipant.getDistrict()
						: event.getEventLocation().getDistrict();
					return vaccinationCriteria.vaccinationAssociationType(VaccinationAssociationType.EVENT_PARTICIPANT)
						.eventParticipantReference(getReference())
						.region(region)
						.district(district);
				}, null, this::showUnsavedChangesPopup, editAllowed)), VACCINATIONS_LOC);
			}
		}

		if (UiUtil.permitted(FeatureType.EXTERNAL_EMAILS, UserRight.EXTERNAL_EMAIL_SEND)) {
			ExternalEmailSideComponent externalEmailSideComponent =
				ExternalEmailSideComponent.forEventParticipant(eventParticipant, editAllowed, this::showUnsavedChangesPopup);
			layout.addSidePanelComponent(new SideComponentLayout(externalEmailSideComponent), EXTERNAL_EMAILS_LOC);
		}

		final boolean deleted = FacadeProvider.getEventParticipantFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, eventParticipantEditAllowed);
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}
}
