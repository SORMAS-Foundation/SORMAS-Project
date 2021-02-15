package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.caze.caselink.CaseListComponent;
import de.symeda.sormas.ui.contact.contactlink.ContactListComponent;
import de.symeda.sormas.ui.events.eventParticipantLink.EventParticipantListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class PersonDataView extends AbstractDetailView<PersonReferenceDto> {

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/data";

	public static final String PERSON_LOC = "person";
	public static final String CASES_LOC = "cases";
	public static final String CONTACTS_LOC = "contacts";
	public static final String EVENT_PARTICIPANTS_LOC = "events";

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
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, EVENT_PARTICIPANTS_LOC));

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

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
			VerticalLayout caseLayout = new VerticalLayout();
			caseLayout.setMargin(false);
			caseLayout.setSpacing(false);

			CaseListComponent caseListComponent = new CaseListComponent(getReference());
			caseListComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			caseLayout.addComponent(caseListComponent);
			layout.addComponent(caseLayout, CASES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CONTACT_TRACING)) {
			VerticalLayout contactLayout = new VerticalLayout();
			contactLayout.setMargin(false);
			contactLayout.setSpacing(false);

			ContactListComponent contactListComponent = new ContactListComponent(getReference());
			contactListComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			contactLayout.addComponent(contactListComponent);
			layout.addComponent(contactLayout, CONTACTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)) {
			VerticalLayout eventParticipantLayout = new VerticalLayout();
			eventParticipantLayout.setMargin(false);
			eventParticipantLayout.setSpacing(false);

			EventParticipantListComponent eventParticipantList = new EventParticipantListComponent(getReference());
			eventParticipantList.addStyleName(CssStyles.SIDE_COMPONENT);
			eventParticipantLayout.addComponent(eventParticipantList);
			layout.addComponent(eventParticipantLayout, EVENT_PARTICIPANTS_LOC);
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
