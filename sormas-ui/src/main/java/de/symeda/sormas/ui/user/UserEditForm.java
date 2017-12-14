package de.symeda.sormas.ui.user;

import java.util.Set;

import com.vaadin.data.Validator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class UserEditForm extends AbstractEditForm<UserDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE_3, "Person data")+
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME),
					LayoutUtil.fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE)
					)+
			LayoutUtil.h3(CssStyles.VSPACE_3, "Adress")+
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowLocs(UserDto.ADDRESS)
					)+
			LayoutUtil.h3(CssStyles.VSPACE_3, "User data")+
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowLocs(UserDto.ACTIVE),
					LayoutUtil.fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES),
					LayoutUtil.fluidRowLocs(UserDto.REGION, UserDto.DISTRICT),
					LayoutUtil.fluidRowLocs(UserDto.HEALTH_FACILITY, UserDto.ASSOCIATED_OFFICER, UserDto.LABORATORY)
					);
    
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
    	addField(UserDto.PHONE, TextField.class);
    	
    	addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);
    	
    	addField(UserDto.ACTIVE, CheckBox.class);
    	addField(UserDto.USER_NAME, TextField.class);
    	addField(UserDto.USER_ROLES, OptionGroup.class);
    	OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
    	userRoles.setMultiSelect(true);
    	userRoles.addItems(UserRole.getAssignableRoles(LoginHelper.getCurrentUserRoles()));
    	
    	ComboBox region = addField(UserDto.REGION, ComboBox.class);

    	ComboBox district = addField(UserDto.DISTRICT, ComboBox.class);
    	region.addValueChangeListener(e -> {
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
    		FieldHelper.updateItems(healthFacility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, false) : null);
    		FieldHelper.updateItems(associatedOfficer, districtDto != null ? FacadeProvider.getUserFacade().getAssignableUsersByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER) : null);
    	});

    	ComboBox laboratory = addField(UserDto.LABORATORY, ComboBox.class);
    	laboratory.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories());
    	
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
    	
    	// associated officer
    	ComboBox associatedOfficer = (ComboBox)getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
    	associatedOfficer.setVisible(isInformant);
    	setRequired(isInformant, UserDto.ASSOCIATED_OFFICER);
    	if (!isInformant) {
    		associatedOfficer.clear();
    	}

    	// health facility
    	ComboBox healthFacility = (ComboBox)getFieldGroup().getField(UserDto.HEALTH_FACILITY);
    	healthFacility.setVisible(isInformant);
    	setRequired(isInformant, UserDto.HEALTH_FACILITY);
    	if (!isInformant) {
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
    	boolean useRegion = isSupervisor || isInformant || isOfficer;
    	region.setVisible(useRegion);
    	setRequired(useRegion, UserDto.REGION);
    	if (!useRegion) {
    		region.clear();
    	}
    	
    	ComboBox district = (ComboBox)getFieldGroup().getField(UserDto.DISTRICT);
    	boolean useDistrict = isInformant || isOfficer;
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
		if(!fnField.isEmpty() && !lnField.isEmpty() && unField.isEmpty()) {
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
	            throw new InvalidValueException("User name is not unique!");
	    }
	}
}
