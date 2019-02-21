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
package de.symeda.sormas.ui.user;

import java.util.Set;

import com.vaadin.data.Validator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

@SuppressWarnings("serial")
public class UserEditForm extends AbstractEditForm<UserDto> {
	
	private static final String USER_EMAIL_DESC_LOC = "userEmailDescLoc";
	private static final String USER_PHONE_DESC_LOC = "userPhoneDescLoc";
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(I18nProperties.getString(Strings.headingPersonData))+
			LayoutUtil.fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME)+
			LayoutUtil.fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE)+
			LayoutUtil.fluidRowLocs(USER_EMAIL_DESC_LOC, USER_PHONE_DESC_LOC)+
			LayoutUtil.h3(I18nProperties.getString(Strings.address)) +
			LayoutUtil.fluidRowLocs(UserDto.ADDRESS) +
			LayoutUtil.h3(I18nProperties.getString(Strings.headingUserData)) +
			LayoutUtil.fluidRowLocs(UserDto.ACTIVE) +
			LayoutUtil.fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES) +
			LayoutUtil.fluidRowLocs(UserDto.REGION, UserDto.DISTRICT, UserDto.COMMUNITY) +
			LayoutUtil.fluidRowLocs(UserDto.HEALTH_FACILITY, UserDto.ASSOCIATED_OFFICER, UserDto.LABORATORY)
			;
    
    public UserEditForm(boolean create, UserRight editOrCreateUserRight) {
        super(UserDto.class, UserDto.I18N_PREFIX, editOrCreateUserRight);

        setWidth(640, Unit.PIXELS);
        
        if (create) {
        	hideValidationUntilNextCommit();
        }
    }

    @Override
	protected void addFields() {

    	addField(UserDto.FIRST_NAME, TextField.class);
    	addField(UserDto.LAST_NAME, TextField.class);
    	addField(UserDto.USER_EMAIL, TextField.class);
    	TextField phone = addField(UserDto.PHONE, TextField.class);
    	phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.phoneNumberValidation)));
    	
    	Label userEmailDesc = new Label(I18nProperties.getString(Strings.infoUserEmail));
    	getContent().addComponent(userEmailDesc, USER_EMAIL_DESC_LOC);
    	Label userPhoneDesc = new Label(I18nProperties.getString(Strings.infoUserPhoneNumber));
    	getContent().addComponent(userPhoneDesc, USER_PHONE_DESC_LOC);
    	    	
    	addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);
    	
    	addField(UserDto.ACTIVE, CheckBox.class);
    	addField(UserDto.USER_NAME, TextField.class);
    	addField(UserDto.USER_ROLES, OptionGroup.class).addValidator(new UserRolesValidator());
    	OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
    	userRoles.setMultiSelect(true);
    	userRoles.addItems(UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles()));
    	
    	ComboBox region = addField(UserDto.REGION, ComboBox.class);
    	ComboBox community = addField(UserDto.COMMUNITY, ComboBox.class);

    	ComboBox district = addField(UserDto.DISTRICT, ComboBox.class);
    	region.addValueChangeListener(e -> {
    		FieldHelper.removeItems(community);
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
       	});

    	
    	// for informant
    	ComboBox associatedOfficer = addField(UserDto.ASSOCIATED_OFFICER, ComboBox.class);

    	ComboBox healthFacility = addField(UserDto.HEALTH_FACILITY, ComboBox.class);
    	district.addValueChangeListener(e -> {
    		FieldHelper.removeItems(healthFacility);
    		FieldHelper.removeItems(associatedOfficer);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
    		FieldHelper.updateItems(healthFacility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, false) : null);
    		FieldHelper.updateItems(associatedOfficer, districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER) : null);
    	});

    	ComboBox laboratory = addField(UserDto.LABORATORY, ComboBox.class);
    	laboratory.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(false));
    	
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

    	setRequired(true, UserDto.FIRST_NAME, UserDto.LAST_NAME, UserDto.USER_NAME, UserDto.USER_ROLES);
    	addValidators(UserDto.USER_NAME, new UserNameValidator());
    	
    	addFieldListeners(UserDto.FIRST_NAME, e -> suggestUserName());
    	addFieldListeners(UserDto.LAST_NAME, e -> suggestUserName());
    	addFieldListeners(UserDto.USER_ROLES, e -> updateFieldsByUserRole());
    	updateFieldsByUserRole();
    }

    @SuppressWarnings("unchecked")
	private void updateFieldsByUserRole() {
    	OptionGroup userRolesField = (OptionGroup)getFieldGroup().getField(UserDto.USER_ROLES);
    	Set<UserRole> userRoles = (Set<UserRole>)userRolesField.getValue();
    	boolean isInformant = UserRole.isInformant(userRoles);
    	boolean isOfficer = UserRole.isOfficer(userRoles);
    	boolean isSupervisor = UserRole.isSupervisor(userRoles);
    	boolean isLabUser = UserRole.isLabUser(userRoles);
    	boolean isStateObserver = userRoles.contains(UserRole.STATE_OBSERVER);
    	boolean isDistrictObserver = userRoles.contains(UserRole.DISTRICT_OBSERVER);
    	
    	// associated officer
    	ComboBox associatedOfficer = (ComboBox)getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
    	associatedOfficer.setVisible(isInformant);
    	setRequired(isInformant, UserDto.ASSOCIATED_OFFICER);
    	if (!isInformant) {
    		associatedOfficer.clear(); 
    	}

    	// community
    	ComboBox community = (ComboBox) getFieldGroup().getField(UserDto.COMMUNITY);
    	community.setVisible(userRoles.contains(UserRole.COMMUNITY_INFORMANT));
    	setRequired(userRoles.contains(UserRole.COMMUNITY_INFORMANT), UserDto.COMMUNITY);
    	if (!userRoles.contains(UserRole.COMMUNITY_INFORMANT)) {
    		community.clear();
    	}
    	
    	// health facility
    	ComboBox healthFacility = (ComboBox)getFieldGroup().getField(UserDto.HEALTH_FACILITY);
    	healthFacility.setVisible(userRoles.contains(UserRole.HOSPITAL_INFORMANT));
    	setRequired(userRoles.contains(UserRole.HOSPITAL_INFORMANT), UserDto.HEALTH_FACILITY);
    	if (!userRoles.contains(UserRole.HOSPITAL_INFORMANT)) {
    		healthFacility.clear();
    	}
    	
    	// laboratory
    	ComboBox laboratory = (ComboBox)getFieldGroup().getField(UserDto.LABORATORY);
    	laboratory.setVisible(isLabUser);
    	setRequired(isLabUser, UserDto.LABORATORY);
    	if (!isLabUser) {
    		laboratory.clear();
    	}
    	
    	ComboBox region = (ComboBox)getFieldGroup().getField(UserDto.REGION);
    	boolean useRegion = isSupervisor || isInformant || isOfficer || isStateObserver || isDistrictObserver;
    	region.setVisible(useRegion);
    	setRequired(useRegion, UserDto.REGION);
    	if (!useRegion) {
    		region.clear();
    	}
    	
    	ComboBox district = (ComboBox)getFieldGroup().getField(UserDto.DISTRICT);
    	boolean useDistrict = isInformant || isOfficer || isDistrictObserver;
    	district.setVisible(useDistrict);
    	setRequired(useDistrict, UserDto.DISTRICT);
    	if (!useDistrict) {
    		district.clear();
    	}
    }

	private void suggestUserName() {
		TextField fnField = (TextField)getFieldGroup().getField(UserDto.FIRST_NAME);
		TextField lnField = (TextField)getFieldGroup().getField(UserDto.LAST_NAME);
		TextField unField = (TextField)getFieldGroup().getField(UserDto.USER_NAME);
		if (!fnField.isEmpty() && !lnField.isEmpty() 
				&& unField.isEmpty() && !unField.isReadOnly()) {
			unField.setValue(UserHelper.getSuggestedUsername(fnField.getValue(), lnField.getValue()));
		}	
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}

	class UserNameValidator implements Validator {
	    @Override
	    public void validate(Object value)
	            throws InvalidValueException {
	    	UserDto dto = getValue();
	        if (!(value instanceof String && ControllerProvider.getUserController().isLoginUnique(dto.getUuid(),(String)value)))
	            throw new InvalidValueException(I18nProperties.getValidationError(Validations.userNameNotUnique));
	    }
	}
}
