package de.symeda.sormas.ui.surveillance.user;

import com.vaadin.data.Validator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.surveillance.location.LocationForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class UserEditForm extends AbstractEditForm<UserDto> {
	
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
					LayoutUtil.fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES)
					);

    public UserEditForm() {
        super(UserDto.class, UserDto.I18N_PREFIX);
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
    	userRoles.addItems(ControllerProvider.getUserController().getUserRoles());
    	
    	
    	setRequired(true, UserDto.FIRST_NAME, UserDto.LAST_NAME, UserDto.USER_NAME);
    	addValidator(UserDto.USER_NAME, new UserNameValidator());
    	
    	addFieldListener(UserDto.FIRST_NAME, e -> suggestUserName());
    	addFieldListener(UserDto.LAST_NAME, e -> suggestUserName());
    }
    
	private void suggestUserName() {
		TextField fnField = (TextField)getFieldGroup().getField(UserDto.FIRST_NAME);
		TextField lnField = (TextField)getFieldGroup().getField(UserDto.LAST_NAME);
		TextField unField = (TextField)getFieldGroup().getField(UserDto.USER_NAME);
		if(!fnField.isEmpty() && !lnField.isEmpty() && unField.isEmpty()) {
			unField.setValue(ControllerProvider.getUserController().getSuggestedUsername(fnField.getValue(), lnField.getValue()));
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
