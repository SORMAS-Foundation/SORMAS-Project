package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class InfrastructureController {

	public InfrastructureController() {

	}

	public void createHealthFacility(boolean laboratory) {
		CommitDiscardWrapperComponent<FacilityEditForm> createComponent = getFacilityEditComponent(null, laboratory);
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create entry");
	}

	public void editHealthFacility(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityEditForm> editComponent = getFacilityEditComponent(facility, facility.getType() == FacilityType.LABORATORY);
		String caption = "Edit " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}
	
	public void createRegion() {
		CommitDiscardWrapperComponent<RegionEditForm> createComponent = getRegionEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create entry");
	}
	
	public void editRegion(String uuid) {
		RegionDto region = FacadeProvider.getRegionFacade().getRegionByUuid(uuid);
		CommitDiscardWrapperComponent<RegionEditForm> editComponent = getRegionEditComponent(region);
		String caption = "Edit " + region.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}
	
	public void createDistrict() {
		CommitDiscardWrapperComponent<DistrictEditForm> createComponent = getDistrictEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create entry");
	}

	public void editDistrict(String uuid) {
		DistrictDto district = FacadeProvider.getDistrictFacade().getDistrictByUuid(uuid);
		CommitDiscardWrapperComponent<DistrictEditForm> editComponent = getDistrictEditComponent(district);
		String caption = "Edit " + district.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createCommunity() {
		CommitDiscardWrapperComponent<CommunityEditForm> createComponent = getCommunityEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create entry");
	}

	public void editCommunity(String uuid) {
		CommunityDto community = FacadeProvider.getCommunityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<CommunityEditForm> editComponent = getCommunityEditComponent(community);
		String caption = "Edit " + community.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	private CommitDiscardWrapperComponent<FacilityEditForm> getFacilityEditComponent(FacilityDto facility, boolean laboratory) {
		FacilityEditForm editForm = new FacilityEditForm(facility == null ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT, 
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
					Notification.show("Entry created", Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(LaboratoriesView.VIEW_NAME);
				} else {
					Notification.show("Entry created", Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(HealthFacilitiesView.VIEW_NAME);
				}
			}
		});

		return editView;
	}
	
	private CommitDiscardWrapperComponent<RegionEditForm> getRegionEditComponent(RegionDto region) {
		RegionEditForm editForm = new RegionEditForm(region == null ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT,
				region == null);
		if (region == null) {
			region = RegionDto.build();
		}
		
		editForm.setValue(region);
		
		final CommitDiscardWrapperComponent<RegionEditForm> editView = new CommitDiscardWrapperComponent<RegionEditForm>(
				editForm, editForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				FacadeProvider.getRegionFacade().saveRegion(editForm.getValue());
				Notification.show("Entry created", Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(RegionsView.VIEW_NAME);
			}
		});
		
		return editView;
	}
	
	private CommitDiscardWrapperComponent<DistrictEditForm> getDistrictEditComponent(DistrictDto district) {
		DistrictEditForm editForm = new DistrictEditForm(district == null ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT,
				district == null);
		if (district == null) {
			district = DistrictDto.build();
		}
		
		editForm.setValue(district);
		
		final CommitDiscardWrapperComponent<DistrictEditForm> editView = new CommitDiscardWrapperComponent<DistrictEditForm>(
				editForm, editForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				FacadeProvider.getDistrictFacade().saveDistrict(editForm.getValue());
				Notification.show("Entry created", Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(DistrictsView.VIEW_NAME);
			}
		});
		
		return editView;
	}
	
	private CommitDiscardWrapperComponent<CommunityEditForm> getCommunityEditComponent(CommunityDto community) {
		CommunityEditForm editForm = new CommunityEditForm(community == null ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT,
				community == null);
		if (community == null) {
			community = CommunityDto.build();
		}
		
		editForm.setValue(community);
		
		final CommitDiscardWrapperComponent<CommunityEditForm> editView = new CommitDiscardWrapperComponent<CommunityEditForm>(
				editForm, editForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				FacadeProvider.getCommunityFacade().saveCommunity(editForm.getValue());
				Notification.show("Entry created", Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME);
			}
		});
		
		return editView;
	}

}
