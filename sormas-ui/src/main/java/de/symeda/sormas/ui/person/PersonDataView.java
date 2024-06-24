package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class PersonDataView extends AbstractEditAllowedDetailView<PersonReferenceDto> implements PersonSideComponentsElement {

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
		boolean isEditAllowed = isEditAllowed();

		setHeightUndefined();
		CommitDiscardWrapperComponent<PersonEditForm> editComponent =
			ControllerProvider.getPersonController().getPersonEditComponent(getReference().getUuid(), isEditAllowed);

		DetailSubComponentWrapper container = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(container, editComponent);
		setSubComponent(container);

		addSideComponents(layout, null, null, getReference(), this::showUnsavedChangesPopup, isEditAllowed);

		setEditPermission(
			editComponent,
			UiUtil.permitted(UserRight.PERSON_EDIT),
			PersonDto.ADDRESSES,
			PersonDto.PERSON_CONTACT_DETAILS);
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
	protected EditPermissionFacade getEditPermissionFacade() {
		return FacadeProvider.getPersonFacade();
	}
}
