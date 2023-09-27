package de.symeda.sormas.ui.immunization;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonSideComponentsElement;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class ImmunizationPersonView extends AbstractImmunizationView implements PersonSideComponentsElement {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public ImmunizationPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		ImmunizationDto immunzation = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(immunzation.getPerson().getUuid());
		CommitDiscardWrapperComponent<PersonEditForm> editComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(PersonContext.IMMUNIZATION, person, immunzation.getDisease(), null, UserRight.PERSON_EDIT, null);
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(
			layout,
			DeletableEntityType.IMMUNIZATION,
			immunzation.getUuid(),
			person.toReference(),
			this::showUnsavedChangesPopup,
			isEditAllowed());
		setEditPermission(editComponent);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}
}
