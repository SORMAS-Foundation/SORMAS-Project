package de.symeda.sormas.ui.immunization;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class ImmunizationPersonView extends AbstractImmunizationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public ImmunizationPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		ImmunizationDto immunzation = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(immunzation.getPerson().getUuid());

		CommitDiscardWrapperComponent<PersonEditForm> immunizationPersonComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(PersonContext.IMMUNIZATION, person, immunzation.getDisease(), null, UserRight.PERSON_EDIT, null);
		setSubComponent(immunizationPersonComponent);

		setEditPermission(immunizationPersonComponent);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}
}
