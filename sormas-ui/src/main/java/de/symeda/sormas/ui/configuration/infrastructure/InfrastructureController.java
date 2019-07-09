/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
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
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editHealthFacility(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityEditForm> editComponent = getFacilityEditComponent(facility, facility.getType() == FacilityType.LABORATORY);
		String caption = I18nProperties.getString(Strings.edit) + " " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}
	
	public void createRegion() {
		CommitDiscardWrapperComponent<RegionEditForm> createComponent = getRegionEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}
	
	public void editRegion(String uuid) {
		RegionDto region = FacadeProvider.getRegionFacade().getRegionByUuid(uuid);
		CommitDiscardWrapperComponent<RegionEditForm> editComponent = getRegionEditComponent(region);
		String caption = I18nProperties.getString(Strings.edit) + " " + region.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}
	
	public void createDistrict() {
		CommitDiscardWrapperComponent<DistrictEditForm> createComponent = getDistrictEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editDistrict(String uuid) {
		DistrictDto district = FacadeProvider.getDistrictFacade().getDistrictByUuid(uuid);
		CommitDiscardWrapperComponent<DistrictEditForm> editComponent = getDistrictEditComponent(district);
		String caption = I18nProperties.getString(Strings.edit) + " " + district.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createCommunity() {
		CommitDiscardWrapperComponent<CommunityEditForm> createComponent = getCommunityEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editCommunity(String uuid) {
		CommunityDto community = FacadeProvider.getCommunityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<CommunityEditForm> editComponent = getCommunityEditComponent(community);
		String caption = I18nProperties.getString(Strings.edit) + " " + community.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}
	
	public void createPointOfEntry() {
		CommitDiscardWrapperComponent<PointOfEntryForm> component = getPointOfEntryEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingCreateEntry));
	}
	
	
	public void editPointOfEntry(String uuid) {
		PointOfEntryDto pointOfEntry = FacadeProvider.getPointOfEntryFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<PointOfEntryForm> component = getPointOfEntryEditComponent(pointOfEntry);
		String caption = I18nProperties.getString(Strings.edit) + " " + pointOfEntry.getName();
		VaadinUiUtil.showModalPopupWindow(component, caption);
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
					Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(LaboratoriesView.VIEW_NAME);
				} else {
					Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
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
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
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
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
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
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME);
			}
		});
		
		return editView;
	}
	
	private CommitDiscardWrapperComponent<PointOfEntryForm> getPointOfEntryEditComponent(PointOfEntryDto pointOfEntry) {
		PointOfEntryForm form = new PointOfEntryForm(pointOfEntry == null ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT, pointOfEntry == null);
		if (pointOfEntry == null) {
			pointOfEntry = PointOfEntryDto.build();
		}
		
		form.setValue(pointOfEntry);
		
		final CommitDiscardWrapperComponent<PointOfEntryForm> view = new CommitDiscardWrapperComponent<PointOfEntryForm>(form, form.getFieldGroup());
		view.addCommitListener(() -> {
			FacadeProvider.getPointOfEntryFacade().save(form.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(PointsOfEntryView.VIEW_NAME);
		});
		
		return view;
	}

}
