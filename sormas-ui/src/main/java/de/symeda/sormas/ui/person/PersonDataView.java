package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.DirtyStateComponent;

public class PersonDataView extends AbstractDetailView<PersonReferenceDto> implements PersonSideComponentsElement {

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/data";

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
		CommitDiscardWrapperComponent<PersonEditForm> editComponent =
			ControllerProvider.getPersonController().getPersonEditComponent(getReference().getUuid(), UserRight.PERSON_EDIT);
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(layout, null, null, getReference(), this::showUnsavedChangesPopup);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(PersonsView.VIEW_NAME, I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, Captions.personPersonsList));
		menu.addView(PersonDataView.VIEW_NAME, I18nProperties.getCaption(PersonDto.I18N_PREFIX), params);

		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(getReference().getUuid());
		setMainHeaderComponent(ControllerProvider.getPersonController().getPersonViewTitleLayout(person));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);
		if (getReference() == null
			|| !UserProvider.getCurrent().hasUserRight(UserRight.PERSON_EDIT)
			|| !FacadeProvider.getPersonFacade().isEditAllowed(getReference().getUuid())) {
			newComponent.setEnabled(false);
		}
	}
}
