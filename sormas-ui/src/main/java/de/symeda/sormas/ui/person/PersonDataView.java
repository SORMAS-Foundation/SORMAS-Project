package de.symeda.sormas.ui.person;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class PersonDataView extends AbstractPersonView implements PersonSideComponentsElement {

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/data";

	public PersonDataView() {
		super(VIEW_NAME);
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

		addSideComponents(layout, null, null, getReference(), null, null, this::showUnsavedChangesPopup, isEditAllowed);

		setEditPermission(editComponent, UiUtil.permitted(UserRight.PERSON_EDIT), PersonDto.ADDRESSES, PersonDto.PERSON_CONTACT_DETAILS);
	}
}
