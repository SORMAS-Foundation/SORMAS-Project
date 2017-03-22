package de.symeda.sormas.ui.contact;

import java.util.List;

import org.joda.time.LocalDate;

import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ContactCreateForm extends AbstractEditForm<ContactDto> {
	
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	
    private static final String HTML_LAYOUT = 
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(ContactDto.CAZE, ContactDto.LAST_CONTACT_DATE),
					LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME),
					LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY),
					LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE),
					LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION),
					LayoutUtil.fluidRowLocs(ContactDto.CONTACT_OFFICER, "")
					);

    public ContactCreateForm() {
        super(ContactDto.class, ContactDto.I18N_PREFIX);

		setWidth(540, Unit.PIXELS);
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
    	addField(ContactDto.RELATION_TO_CASE, ComboBox.class);

    	ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
    	contactOfficerField.setNullSelectionAllowed(true);
    	
    	setRequired(true, ContactDto.CAZE, FIRST_NAME, LAST_NAME, ContactDto.LAST_CONTACT_DATE, ContactDto.CONTACT_PROXIMITY, ContactDto.RELATION_TO_CASE);
    	
    	addValueChangeListener(e -> {
    		updateLastContactDateValidator();
    		
    		// set assignable officers
    		ContactDto contactDto = getValue();
        	if (contactDto != null) {
    	    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
    	    	contactOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsersByDistrict(caseDto.getDistrict(), UserRole.CONTACT_OFFICER));
        	}
    	});
    }
    
    protected void updateLastContactDateValidator() {
    	Field<?> dateField = getField(ContactDto.LAST_CONTACT_DATE);
    	dateField.removeAllValidators();
    	if (getValue() != null) {
	    	dateField.addValidator(new DateRangeValidator("Date of last contact has to be before date of report",
	    			null, new LocalDate(getValue().getReportDateTime()).plusDays(1).toDate(), Resolution.SECOND));
    	}
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
