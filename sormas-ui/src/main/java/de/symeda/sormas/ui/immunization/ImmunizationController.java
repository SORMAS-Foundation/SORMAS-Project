package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.immunization.components.ImmunizationCreationForm;
import de.symeda.sormas.ui.immunization.components.TitleLayout;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ImmunizationController {

	public void registerViews(Navigator navigator) {
		navigator.addView(ImmunizationsView.VIEW_NAME, ImmunizationsView.class);
		navigator.addView(ImmunizationDataView.VIEW_NAME, ImmunizationDataView.class);
		navigator.addView(ImmunizationPersonView.VIEW_NAME, ImmunizationPersonView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<ImmunizationCreationForm> immunizationCreateComponent = getImmunizationCreateComponent();
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	public void navigateToImmunization(String uuid) {
		final String navigationState = ImmunizationDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent() {
		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null) {
			ImmunizationCreationForm createForm = new ImmunizationCreationForm();
			ImmunizationDto immunization = ImmunizationDto.build(null);
			immunization.setReportingUser(currentUserProvider.getUserReference());
			createForm.setValue(immunization);
			final CommitDiscardWrapperComponent<ImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
				createForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				createForm.getFieldGroup());

			viewComponent.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {

					final ImmunizationDto dto = createForm.getValue();
					final PersonDto person = createForm.getPerson();
					ControllerProvider.getPersonController()
						.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForImmunization), selectedPerson -> {
							if (selectedPerson != null) {
								dto.setPerson(selectedPerson);
								FacadeProvider.getImmunizationFacade().save(dto);
							}
						}, true);
				}
			});
			return viewComponent;
		}
		return null;
	}

	public VerticalLayout getImmunizationViewTitleLayout(String uuid) {
		ImmunizationDto immunizationDto = findimmunization(uuid);

		String shortUuid = DataHelper.getShortUuid(immunizationDto.getUuid());
		PersonReferenceDto person = immunizationDto.getPerson();
		String text = person.getFirstName() + " " + person.getLastName() + " (" + shortUuid + ")";

		return new TitleLayout(text);
	}

	private ImmunizationDto findimmunization(String uuid) {
		return FacadeProvider.getImmunizationFacade().getByUuid(uuid);
	}
}
