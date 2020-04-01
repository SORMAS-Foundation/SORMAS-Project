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

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;

import java.util.Set;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

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
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class UserEditForm extends AbstractEditForm<UserDto> {
		
		private static final long serialVersionUID = 1L;
		
	
	private static final String USER_EMAIL_DESC_LOC = "userEmailDescLoc";
	private static final String USER_PHONE_DESC_LOC = "userPhoneDescLoc";
	
    private static final String HTML_LAYOUT = 
    		h3(I18nProperties.getString(Strings.headingPersonData)) +
			fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME) +
			fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE) +
			fluidRowLocs(USER_EMAIL_DESC_LOC, USER_PHONE_DESC_LOC) +
			fluidRowLocsCss(VSPACE_TOP_3, UserDto.LANGUAGE, "") +
			
			h3(I18nProperties.getString(Strings.address)) +
			fluidRowLocs(UserDto.ADDRESS) +
			
			h3(I18nProperties.getString(Strings.headingUserData)) +
			fluidRowLocs(UserDto.ACTIVE) +
			fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES) +
			fluidRowLocs(UserDto.REGION, UserDto.DISTRICT, UserDto.COMMUNITY) +
			fluidRowLocs(UserDto.HEALTH_FACILITY, UserDto.POINT_OF_ENTRY, UserDto.ASSOCIATED_OFFICER, UserDto.LABORATORY) +
			fluidRowLocs(UserDto.LIMITED_DISEASE, "", "");
    
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
    	addDiseaseField(UserDto.LIMITED_DISEASE, false);
    	
    	Label userEmailDesc = new Label(I18nProperties.getString(Strings.infoUserEmail));
    	getContent().addComponent(userEmailDesc, USER_EMAIL_DESC_LOC);
    	Label userPhoneDesc = new Label(I18nProperties.getString(Strings.infoUserPhoneNumber));
    	getContent().addComponent(userPhoneDesc, USER_PHONE_DESC_LOC);

		ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
		CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
		ControllerProvider.getUserController().setFlagIcons(cbLanguage);
    	    	
    	addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);
    	
    	addField(UserDto.ACTIVE, CheckBox.class);
    	addField(UserDto.USER_NAME, TextField.class);
    	addField(UserDto.USER_ROLES, OptionGroup.class).addValidator(new UserRolesValidator());
    	OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
    	userRoles.setMultiSelect(true);
    	userRoles.addItems(UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles()));
    	
    	ComboBox region = addInfrastructureField(UserDto.REGION);
    	ComboBox community = addInfrastructureField(UserDto.COMMUNITY);

    	ComboBox district = addInfrastructureField(UserDto.DISTRICT);
    	region.addValueChangeListener(e -> {
    		FieldHelper.removeItems(community);
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
       	});

    	
    	// for informant
    	ComboBox associatedOfficer = addField(UserDto.ASSOCIATED_OFFICER, ComboBox.class);

    	ComboBox healthFacility = addInfrastructureField(UserDto.HEALTH_FACILITY);
    	ComboBox cbPointOfEntry = addInfrastructureField(UserDto.POINT_OF_ENTRY);
    	district.addValueChangeListener(e -> {
    		FieldHelper.removeItems(healthFacility);
    		FieldHelper.removeItems(associatedOfficer);
    		FieldHelper.removeItems(cbPointOfEntry);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
    		FieldHelper.updateItems(healthFacility, districtDto != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, false) : null);
    		FieldHelper.updateItems(associatedOfficer, districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER) : null);
    		FieldHelper.updateItems(cbPointOfEntry, districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), false) : null);
    	});

    	ComboBox laboratory = addField(UserDto.LABORATORY, ComboBox.class);
    	laboratory.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false));
    	
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

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
    	boolean isPortHealthUser = UserRole.isPortHealthUser(userRoles);
    	boolean isStateObserver = userRoles.contains(UserRole.STATE_OBSERVER);
    	boolean isDistrictObserver = userRoles.contains(UserRole.DISTRICT_OBSERVER);
    	
    	// associated officer
    	ComboBox associatedOfficer = (ComboBox)getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
    	associatedOfficer.setVisible(isInformant);
    	setRequired(isInformant && !isPortHealthUser, UserDto.ASSOCIATED_OFFICER);
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
    	healthFacility.setVisible(isOfficer || userRoles.contains(UserRole.HOSPITAL_INFORMANT));
    	setRequired(userRoles.contains(UserRole.HOSPITAL_INFORMANT), UserDto.HEALTH_FACILITY);
    	if (!healthFacility.isVisible()) {
    		healthFacility.clear();
    	}
    	
    	// laboratory
    	ComboBox laboratory = (ComboBox)getFieldGroup().getField(UserDto.LABORATORY);
    	laboratory.setVisible(isLabUser);
    	setRequired(isLabUser, UserDto.LABORATORY);
    	if (!isLabUser) {
    		laboratory.clear();
    	}
    	
    	// point of entry
    	ComboBox pointOfEntry = (ComboBox) getFieldGroup().getField(UserDto.POINT_OF_ENTRY);
    	boolean usePointOfEntry = isPortHealthUser && isInformant;
    	pointOfEntry.setVisible(usePointOfEntry);
    	setRequired(usePointOfEntry, UserDto.POINT_OF_ENTRY);
    	if (!usePointOfEntry) {
    		pointOfEntry.clear();
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
		
		private static final long serialVersionUID = 1L;
		
		@Override
	    public void validate(Object value)
	            throws InvalidValueException {
	    	UserDto dto = getValue();
	        if (!(value instanceof String && ControllerProvider.getUserController().isLoginUnique(dto.getUuid(),(String)value)))
	            throw new InvalidValueException(I18nProperties.getValidationError(Validations.userNameNotUnique));
	    }
	}
}
