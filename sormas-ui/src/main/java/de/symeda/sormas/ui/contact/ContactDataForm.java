package de.symeda.sormas.ui.contact;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
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
public class ContactDataForm extends AbstractEditForm<ContactDto> {
	
	private static final String CASE_INFO = "caseInfo";

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Contact data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE2, 
		    		LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
		    				LayoutUtil.fluidColumn(8, 0, 
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_STATUS) +
		    						LayoutUtil.fluidRowLocs(ContactDto.UUID, ContactDto.REPORTING_USER) +
		    						LayoutUtil.fluidRowLocs(ContactDto.LAST_CONTACT_DATE, ContactDto.REPORTING_USER) +
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
				    				LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION) +
				    				LayoutUtil.fluidRowLocs(ContactDto.CONTACT_OFFICER, "")
		    						),
		    				LayoutUtil.fluidColumnLoc(4, 0,  CASE_INFO)
		    		)
		    );

    public ContactDataForm() {
        super(ContactDto.class, ContactDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(ContactDto.CONTACT_STATUS, OptionGroup.class);
    	addField(ContactDto.UUID, TextField.class);
    	addField(ContactDto.REPORTING_USER, ComboBox.class);
    	addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
    	addField(ContactDto.REPORT_DATE_TIME, DateField.class);
    	addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
    	
		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
    	addField(ContactDto.CONTACT_OFFICER, ComboBox.class)
    		.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.CONTACT_OFFICER));
    	
    	setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME);
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
