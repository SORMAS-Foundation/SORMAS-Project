package de.symeda.sormas.ui.user;

import java.util.Set;

import com.vaadin.data.Validator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationForm;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class UserEditForm extends AbstractEditForm<UserDto> {
	
	private static final String NEW_PASSWORD = "newPassword";
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Person data")+
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME),
					LayoutUtil.fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE)
					)+
			LayoutUtil.h3(CssStyles.VSPACE3, "Adress")+
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(UserDto.ADDRESS)
					)+
			LayoutUtil.h3(CssStyles.VSPACE3, "User data")+
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(UserDto.ACTIVE),
					LayoutUtil.fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES),
					LayoutUtil.fluidRowLocs(UserDto.REGION, UserDto.DISTRICT),
					LayoutUtil.fluidRowLocs(UserDto.HEALTH_FACILITY, UserDto.ASSOCIATED_OFFICER),
					LayoutUtil.fluidRowLocs(NEW_PASSWORD)
					);

    public UserEditForm() {
        super(UserDto.class, UserDto.I18N_PREFIX);

        setWidth(540, Unit.PIXELS);
    }

    @Override
	protected void addFields() {

    	addField(UserDto.FIRST_NAME, TextField.class);
    	addField(UserDto.LAST_NAME, TextField.class);
    	addField(UserDto.USER_EMAIL, TextField.class);
    	addField(UserDto.PHONE, TextField.class);
    	
    	addField(UserDto.ADDRESS, LocationForm.class).setCaption(null);
    	
    	addField(UserDto.ACTIVE, CheckBox.class);
    	addField(UserDto.USER_NAME, TextField.class);
    	addField(UserDto.USER_ROLES, OptionGroup.class);
    	OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
    	userRoles.setMultiSelect(true);
    	userRoles.addItems(UserRole.getAssignableRoles(LoginHelper.getCurrentUserRoles()));
    	
    	Button newPasswordButton = new Button(null, FontAwesome.UNLOCK_ALT);
    	newPasswordButton.setCaption("Create new password");
    	newPasswordButton.addStyleName(ValoTheme.BUTTON_LINK);
    	newPasswordButton.addClickListener(e -> newPasswordClicked());
    	getContent().addComponent(newPasswordButton, NEW_PASSWORD);

    	ComboBox region = addField(UserDto.REGION, ComboBox.class);

    	ComboBox district = addField(UserDto.DISTRICT, ComboBox.class);
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});

    	ComboBox healthFacility = addField(UserDto.HEALTH_FACILITY, ComboBox.class);
    	district.addValueChangeListener(e -> {
    		healthFacility.removeAllItems();
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		if (districtDto != null) {
    			healthFacility.addItems(FacadeProvider.getFacilityFacade().getAllByDistrict(districtDto));
    		}
    	});

		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

    	// for informant
    	ComboBox associatedOfficer = addField(UserDto.ASSOCIATED_OFFICER, ComboBox.class);
    	associatedOfficer.addItems(FacadeProvider.getUserFacade().getAssignableUsers(
    			LoginHelper.getCurrentUserAsReference(), UserRole.SURVEILLANCE_OFFICER));
    	
    	setRequired(true, UserDto.FIRST_NAME, UserDto.LAST_NAME, UserDto.USER_NAME, UserDto.USER_ROLES,
    			UserDto.REGION, UserDto.DISTRICT);
    	addValidators(UserDto.USER_NAME, new UserNameValidator());
    	
    	addFieldListeners(UserDto.FIRST_NAME, e -> suggestUserName());
    	addFieldListeners(UserDto.LAST_NAME, e -> suggestUserName());
    	addFieldListeners(UserDto.USER_ROLES, e -> updateFieldsByUserRole());
    	updateFieldsByUserRole();
    }

    private void updateFieldsByUserRole() {
    	OptionGroup userRolesField = (OptionGroup)getFieldGroup().getField(UserDto.USER_ROLES);
    	Set<UserRole> userRoles = (Set<UserRole>)userRolesField.getValue();
    	boolean isInformant = userRoles.contains(UserRole.INFORMANT);
    	
    	// associated officer
    	ComboBox associatedOfficer = (ComboBox)getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
    	associatedOfficer.setVisible(isInformant);
    	associatedOfficer.setRequired(isInformant);
    	if (!isInformant) {
    		associatedOfficer.clear();
    	}

    	// health facility
    	ComboBox healthFacility = (ComboBox)getFieldGroup().getField(UserDto.HEALTH_FACILITY);
    	healthFacility.setVisible(isInformant);
    	healthFacility.setRequired(isInformant);
    	if (!isInformant) {
    		healthFacility.clear();
    	}
    }
    
	private void newPasswordClicked() {
		UserDto dto = getValue();
		ControllerProvider.getUserController().confirmNewPassword(dto.getUuid());
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
