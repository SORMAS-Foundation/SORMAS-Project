package de.symeda.sormas.ui.contact;

import org.joda.time.LocalDate;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ContactCreateForm extends AbstractEditForm<ContactDto> {
	
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	
    private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(ContactDto.CAZE, ContactDto.LAST_CONTACT_DATE) +
			LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME) +
			LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
			LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
			LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION) +
			LayoutUtil.fluidRowLocs(ContactDto.CONTACT_OFFICER, "")
			;

    public ContactCreateForm(UserRight editOrCreateUserRight) {
        super(ContactDto.class, ContactDto.I18N_PREFIX, editOrCreateUserRight);

		setWidth(540, Unit.PIXELS);
		
		hideValidationUntilNextCommit();
    }
    
    @Override
	protected void addFields() {

    	TextField firstName = addCustomField(FIRST_NAME, String.class, TextField.class);
    	TextField lastName = addCustomField(LAST_NAME, String.class, TextField.class);
    	
    	ComboBox caze = addField(ContactDto.CAZE, ComboBox.class);
    	caze.addItems(FacadeProvider.getCaseFacade().getSelectableCases(LoginHelper.getCurrentUserAsReference()));

    	DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
    	OptionGroup contactProximity = addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
    	contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
    	addField(ContactDto.DESCRIPTION, TextArea.class).setRows(2);
    	ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
    	
    	CssStyles.style(CssStyles.SOFT_REQUIRED, firstName, lastName, caze, lastContactDate, contactProximity, relationToCase);

    	ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
    	contactOfficerField.setNullSelectionAllowed(true);
    	
    	setRequired(true, ContactDto.CAZE, FIRST_NAME, LAST_NAME);
    	
    	addValueChangeListener(e -> {
    		updateLastContactDateValidator();
    		
    		// set assignable officers
    		ContactDto contactDto = getValue();
        	if (contactDto != null) {
    	    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
    	    	contactOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsersByDistrict(caseDto.getDistrict(), false, UserRole.CONTACT_OFFICER));
        	}
    	});
    }
    
    protected void updateLastContactDateValidator() {
    	Field<?> dateField = getField(ContactDto.LAST_CONTACT_DATE);
    	for (Validator validator : dateField.getValidators()) {
    		if (validator instanceof DateRangeValidator) {
    			dateField.removeValidator(validator);
    		}
    	}
    	if (getValue() != null) {
	    	dateField.addValidator(new DateRangeValidator("Date of last contact has to be before date of report",
	    			null, new LocalDate(getValue().getReportDateTime()).toDate(), Resolution.SECOND));
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
