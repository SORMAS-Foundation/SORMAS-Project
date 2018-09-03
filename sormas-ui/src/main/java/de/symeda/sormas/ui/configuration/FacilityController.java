package de.symeda.sormas.ui.configuration;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class FacilityController {

	public FacilityController() {

	}

	public void create(String caption, boolean laboratory) {
		CommitDiscardWrapperComponent<FacilityEditForm> caseCreateComponent = getFacilityEditComponent(null, laboratory);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, caption);
	}

	public void edit(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityEditForm> caseEditComponent = getFacilityEditComponent(facility, facility.getType() == FacilityType.LABORATORY);
		String caption = "Edit " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(caseEditComponent, caption);
	}

	private CommitDiscardWrapperComponent<FacilityEditForm> getFacilityEditComponent(FacilityDto facility, boolean laboratory) {
		FacilityEditForm editForm = new FacilityEditForm(facility == null ? UserRight.FACILITIES_CREATE : UserRight.FACILITIES_EDIT, 
				facility == null, laboratory);
		if (facility == null) {
			facility = FacilityDto.build();
			if (laboratory) {
				facility.setType(FacilityType.LABORATORY);
			}
		}

		editForm.setValue(facility);

		final CommitDiscardWrapperComponent<FacilityEditForm> editView = new CommitDiscardWrapperComponent<FacilityEditForm>(
				editForm, editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				FacadeProvider.getFacilityFacade().saveFacility(editForm.getValue());
				if (laboratory) {
					Notification.show("New laboratory created", Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(LaboratoriesView.VIEW_NAME);
				} else {
					Notification.show("New health facility created", Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(HealthFacilitiesView.VIEW_NAME);
				}
			}
		});

		return editView;
	}

}
