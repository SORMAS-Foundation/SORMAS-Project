package de.symeda.sormas.ui.contact;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ContactCreateForm extends AbstractEditForm<ContactDto> {
	
	private static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	
    private static final String HTML_LAYOUT = 
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(ContactDto.CAZE, ContactDto.LAST_CONTACT_DATE),
					LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME),
					LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY),
					LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION),
					LayoutUtil.fluidRowLocs(ContactDto.CONTACT_OFFICER, "")
					);

    public ContactCreateForm() {
        super(ContactDto.class, ContactDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {

    	addCustomField(FIRST_NAME, String.class, TextField.class);
    	addCustomField(LAST_NAME, String.class, TextField.class);
    	
    	addField(ContactDto.CAZE, ComboBox.class)
    		.addItems(FacadeProvider.getCaseFacade().getSelectableCases(LoginHelper.getCurrentUserAsReference()));

    	addField(ContactDto.LAST_CONTACT_DATE);
    	addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
    	addField(ContactDto.DESCRIPTION, TextArea.class).setRows(2);

		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
    	addField(ContactDto.CONTACT_OFFICER, ComboBox.class)    	
    		.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.CONTACT_OFFICER));

    	setRequired(true, ContactDto.CAZE, FIRST_NAME, LAST_NAME, ContactDto.LAST_CONTACT_DATE, ContactDto.CONTACT_PROXIMITY);
    }
    
    public String getPersonFirstName() {
    	return (String)getField(FIRST_NAME).getValue();
    }

    public String getPersonLastName() {
    	return (String)getField(LAST_NAME).getValue();
    }

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
