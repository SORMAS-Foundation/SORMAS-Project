package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.caselink.CaseListComponent;
import de.symeda.sormas.ui.contact.contactlink.ContactListComponent;
import de.symeda.sormas.ui.events.eventParticipantLink.EventParticipantListComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
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
	String EVENT_PARTICIPANTS_LOC = "events";
	String TRAVEL_ENTRIES_LOC = "travelEntries";
	String IMMUNIZATION_LOC = "immunizations";
	String VACCINATIONS_LOC = "vaccinations";

	default String getHtmlLayout() {
		return LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, PERSON_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LOC),
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
		CoreEntityType entityType,
		String entityUuid,
		PersonReferenceDto person,
		Consumer<Runnable> showUnsavedChangesPopup) {

		UserProvider currentUser = UserProvider.getCurrent();

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.CASE_VIEW)) {
			layout.addComponent(
				new SideComponentLayout(
					new CaseListComponent(person, entityType == CoreEntityType.CASE ? entityUuid : null, showUnsavedChangesPopup)),
				CASES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.CONTACT_VIEW)) {
			layout.addComponent(
				new SideComponentLayout(
					new ContactListComponent(person, entityType == CoreEntityType.CONTACT ? entityUuid : null, showUnsavedChangesPopup)),
				CONTACTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.EVENT_VIEW)
			&& currentUser.hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {
			layout.addComponent(
				new SideComponentLayout(
					new EventParticipantListComponent(
						person,
						entityType == CoreEntityType.EVENT_PARTICIPANT ? entityUuid : null,
						showUnsavedChangesPopup)),
				EVENT_PARTICIPANTS_LOC);
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
						entityType == CoreEntityType.TRAVEL_ENTRY ? entityUuid : null,
						showUnsavedChangesPopup)),
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
							entityType == CoreEntityType.IMMUNIZATION ? entityUuid : null,
							showUnsavedChangesPopup)),
					IMMUNIZATION_LOC);
			} else {
				layout.addComponent(
					new SideComponentLayout(
						new VaccinationListComponent(
							() -> new VaccinationCriteria.Builder(person).build(),
							entityType == CoreEntityType.IMMUNIZATION ? entityUuid : null,
							showUnsavedChangesPopup,
							false,
							true)),
					VACCINATIONS_LOC);
			}
		}
	}

}
