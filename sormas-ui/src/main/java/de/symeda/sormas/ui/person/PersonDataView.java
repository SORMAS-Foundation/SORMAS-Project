package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.caselink.CaseListComponent;
import de.symeda.sormas.ui.contact.contactlink.ContactListComponent;
import de.symeda.sormas.ui.events.eventParticipantLink.EventParticipantListComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.travelentry.travelentrylink.TravelEntryListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

public class PersonDataView extends AbstractDetailView<PersonReferenceDto> {

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/data";

	public static final String PERSON_LOC = "person";
	public static final String CASES_LOC = "cases";
	public static final String CONTACTS_LOC = "contacts";
	public static final String EVENT_PARTICIPANTS_LOC = "events";
	public static final String TRAVEL_ENTRIES_LOC = "travelEntries";
	public static final String IMMUNIZATION_LOC = "immunizations";
	public static final String VACCINATIONS_LOC = "vaccinations";

	private CommitDiscardWrapperComponent<PersonEditForm> editComponent;

	public PersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected PersonReferenceDto getReferenceByUuid(String uuid) {
		final PersonReferenceDto reference;
		if (FacadeProvider.getPersonFacade().exists(uuid)) {
			reference = FacadeProvider.getPersonFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return PersonsView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, PERSON_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, EVENT_PARTICIPANTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TRAVEL_ENTRIES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, IMMUNIZATION_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, VACCINATIONS_LOC));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		editComponent = ControllerProvider.getPersonController().getPersonEditComponent(getReference().getUuid(), UserRight.PERSON_EDIT);
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, PERSON_LOC);

		if (FacadeProvider.getPersonFacade().isSharedWithoutOwnership(getReference().getUuid())) {
			editComponent.setEnabled(false);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
			layout.addComponent(new SideComponentLayout(new CaseListComponent(getReference())), CASES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING)
			&& UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			layout.addComponent(new SideComponentLayout(new ContactListComponent(getReference())), CONTACTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)) {
			layout.addComponent(new SideComponentLayout(new EventParticipantListComponent(getReference())), EVENT_PARTICIPANTS_LOC);
		}

		UserProvider currentUser = UserProvider.getCurrent();
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TRAVEL_ENTRIES)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {
			TravelEntryListCriteria travelEntryListCriteria = new TravelEntryListCriteria.Builder().withPerson(getReference()).build();
			layout.addComponent(
				new SideComponentLayout(new TravelEntryListComponent(travelEntryListCriteria, this::showUnsavedChangesPopup)),
				TRAVEL_ENTRIES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.IMMUNIZATION_VIEW)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				final ImmunizationListCriteria immunizationListCriteria = new ImmunizationListCriteria.Builder(getReference()).build();
				layout.addComponent(
					new SideComponentLayout(new ImmunizationListComponent(immunizationListCriteria, this::showUnsavedChangesPopup)),
					IMMUNIZATION_LOC);
			} else {
				VaccinationListCriteria criteria = new VaccinationListCriteria.Builder(getReference()).build();
				layout.addComponent(new SideComponentLayout(new VaccinationListComponent(criteria)), VACCINATIONS_LOC);
			}
		}
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(PersonsView.VIEW_NAME, I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, Captions.personPersonsList));
		menu.addView(PersonDataView.VIEW_NAME, I18nProperties.getCaption(PersonDto.I18N_PREFIX), params);

		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(getReference().getUuid());
		setMainHeaderComponent(ControllerProvider.getPersonController().getPersonViewTitleLayout(person));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}
}
