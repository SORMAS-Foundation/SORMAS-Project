package de.symeda.sormas.ui.configuration;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * @author Christopher Riedel
 */
public class FacilityController {

	public FacilityController() {

	}

	public void create(String caption, boolean laboratory) {
		CommitDiscardWrapperComponent<FacilityCreateForm> caseCreateComponent = getFacilityCreateComponent(laboratory);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, caption);
	}

	public CommitDiscardWrapperComponent<FacilityCreateForm> getFacilityCreateComponent(boolean laboratory) {

		FacilityCreateForm createForm = new FacilityCreateForm(UserRight.FACILITIES_CREATE, laboratory);
		FacilityDto facility = FacilityDto.build();
		if (laboratory) {
			facility.setType(FacilityType.LABORATORY);
		}
		createForm.setValue(facility);

		final CommitDiscardWrapperComponent<FacilityCreateForm> createView = new CommitDiscardWrapperComponent<FacilityCreateForm>(
				createForm, createForm.getFieldGroup());

		createView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				saveFacility(createForm.getValue());
			}
		});

		return createView;
	}

	public void edit(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityCreateForm> caseEditComponent = getFacilityEditComponent(facility, facility.getType() == FacilityType.LABORATORY);
		String caption = "Edit " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(caseEditComponent, caption);
	}

	private CommitDiscardWrapperComponent<FacilityCreateForm> getFacilityEditComponent(FacilityDto facility, boolean laboratory) {
		FacilityCreateForm editForm = new FacilityCreateForm(UserRight.FACILITIES_EDIT, laboratory);
		editForm.setValue(facility);

		final CommitDiscardWrapperComponent<FacilityCreateForm> editView = new CommitDiscardWrapperComponent<FacilityCreateForm>(
				editForm, editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				saveFacility(editForm.getValue());
			}
		});

		return editView;
	}

	protected void saveFacility(FacilityDto value) {
		FacadeProvider.getFacilityFacade().saveFacility(value);
	}
}
