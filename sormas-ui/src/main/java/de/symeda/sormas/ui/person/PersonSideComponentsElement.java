package de.symeda.sormas.ui.person;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseListEntryDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.event.EventParticipantListEntryDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.caselink.CaseListComponent;
import de.symeda.sormas.ui.contact.contactlink.ContactListComponent;
import de.symeda.sormas.ui.events.eventParticipantLink.EventParticipantListComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.travelentry.travelentrylink.TravelEntryListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

public interface PersonSideComponentsElement {

	String PERSON_LOC = "person";
	String CASES_LOC = "cases";
	String CONTACTS_LOC = "contacts";
	String SAMPLES_LOC = "samples";
	String EVENT_PARTICIPANTS_LOC = "events";
	String TRAVEL_ENTRIES_LOC = "travelEntries";
	String IMMUNIZATION_LOC = "immunizations";
	String VACCINATIONS_LOC = "vaccinations";

	default String getHtmlLayout() {
		return LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, PERSON_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, EVENT_PARTICIPANTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TRAVEL_ENTRIES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, IMMUNIZATION_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, VACCINATIONS_LOC));
	}

	default DetailSubComponentWrapper addComponentWrapper(CommitDiscardWrapperComponent<PersonEditForm> personComponent) {

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> personComponent);
		container.setWidth(100, Sizeable.Unit.PERCENTAGE);
		container.setMargin(true);
		return container;
	}

	default CustomLayout addPageLayout(DetailSubComponentWrapper container, CommitDiscardWrapperComponent<PersonEditForm> personComponent) {

		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(getHtmlLayout());
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);
		personComponent.setMargin(false);
		personComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
		personComponent.getWrappedComponent().setWidth(100, Sizeable.Unit.PERCENTAGE);
		personComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(personComponent, PERSON_LOC);
		return layout;
	}

	default void addSideComponents(
		CustomLayout layout,
		DeletableEntityType entityType,
		String entityUuid,
		PersonReferenceDto person,
		Consumer<Runnable> showUnsavedChangesPopup,
		boolean isEditAllowed) {

		UserProvider currentUser = UserProvider.getCurrent();
		CaseListComponent caseListComponent = null;
		ContactListComponent contactListComponent = null;
		EventParticipantListComponent eventParticipantListComponent = null;

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.CASE_VIEW)) {
			caseListComponent =
				new CaseListComponent(person, entityType == DeletableEntityType.CASE ? entityUuid : null, showUnsavedChangesPopup, isEditAllowed);
			layout.addComponent(new SideComponentLayout(caseListComponent), CASES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.CONTACT_VIEW)) {
			contactListComponent = new ContactListComponent(
				person,
				entityType == DeletableEntityType.CONTACT ? entityUuid : null,
				showUnsavedChangesPopup,
				isEditAllowed);
			layout.addComponent(new SideComponentLayout(contactListComponent), CONTACTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.EVENT_VIEW)
			&& currentUser.hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {
			eventParticipantListComponent = new EventParticipantListComponent(
				person,
				entityType == DeletableEntityType.EVENT_PARTICIPANT ? entityUuid : null,
				showUnsavedChangesPopup,
				isEditAllowed);
			layout.addComponent(new SideComponentLayout(eventParticipantListComponent), EVENT_PARTICIPANTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.SAMPLES_LAB)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.SAMPLE_VIEW)
			//restricts the sample component to be shown only on Person View
			&& getClass().equals(PersonDataView.class)) {

			List<String> caseList = caseListComponent == null
				? null
				: caseListComponent.getEntries().stream().map(CaseListEntryDto::getUuid).collect(Collectors.toList());

			List<String> contactList = contactListComponent == null
				? null
				: contactListComponent.getEntries().stream().map(ContactListEntryDto::getUuid).collect(Collectors.toList());

			List<String> eventParticipantList = eventParticipantListComponent == null
				? null
				: eventParticipantListComponent.getEntries().stream().map(EventParticipantListEntryDto::getUuid).collect(Collectors.toList());

			SampleCriteria sampleCriteria = new SampleCriteria();
			sampleCriteria.caseUuids(caseList)
				.contactUuids(contactList)
				.eventParticipantUuids(eventParticipantList)
				.sampleAssociationType(SampleAssociationType.PERSON);

			SampleListComponent sampleList =
				new SampleListComponent(sampleCriteria, showUnsavedChangesPopup, isEditAllowed, SampleAssociationType.PERSON);
			SampleListComponentLayout sampleListComponentLayout = new SampleListComponentLayout(sampleList, null, isEditAllowed);
			layout.addComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TRAVEL_ENTRIES)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			TravelEntryListCriteria travelEntryListCriteria = new TravelEntryListCriteria.Builder().withPerson(person).build();
			layout.addComponent(
				new SideComponentLayout(
					new TravelEntryListComponent(
						travelEntryListCriteria,
						entityType == DeletableEntityType.TRAVEL_ENTRY ? entityUuid : null,
						showUnsavedChangesPopup,
						isEditAllowed)),
				TRAVEL_ENTRIES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.IMMUNIZATION_VIEW)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				layout.addComponent(
					new SideComponentLayout(
						new ImmunizationListComponent(
							() -> new ImmunizationListCriteria.Builder(person).build(),
							entityType == DeletableEntityType.IMMUNIZATION ? entityUuid : null,
							showUnsavedChangesPopup,
							isEditAllowed)),
					IMMUNIZATION_LOC);
			} else {
				layout.addComponent(
					new SideComponentLayout(
						new VaccinationListComponent(
							() -> new VaccinationCriteria.Builder(person).build(),
							entityType == DeletableEntityType.IMMUNIZATION ? entityUuid : null,
							showUnsavedChangesPopup,
							false,
							isEditAllowed)),
					VACCINATIONS_LOC);
			}
		}
	}

}
