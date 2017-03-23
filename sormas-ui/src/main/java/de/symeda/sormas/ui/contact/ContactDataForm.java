package de.symeda.sormas.ui.contact;

import org.joda.time.LocalDate;

import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ContactDataForm extends AbstractEditForm<ContactDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Contact data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE2, 
		    		LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_CLASSIFICATION) +
		    						LayoutUtil.fluidRowLocs(ContactDto.LAST_CONTACT_DATE, ContactDto.UUID) +
		    						LayoutUtil.fluidRowLocs(ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME) +
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
		    						LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
				    				LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION) +
				    				LayoutUtil.fluidRowLocs(ContactDto.FOLLOW_UP_STATUS, ContactDto.FOLLOW_UP_UNTIL) +
				    				LayoutUtil.fluidRowLocs(ContactDto.CONTACT_OFFICER, "")
		    						)
		    		);

    public ContactDataForm() {
        super(ContactDto.class, ContactDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(ContactDto.CONTACT_CLASSIFICATION, OptionGroup.class);
    	addField(ContactDto.UUID, TextField.class);
    	addField(ContactDto.REPORTING_USER, ComboBox.class);
    	addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
    	addField(ContactDto.REPORT_DATE_TIME, DateField.class);
    	addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
    	addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
    	addField(ContactDto.DESCRIPTION, TextArea.class).setRows(3);

    	addField(ContactDto.FOLLOW_UP_STATUS, ComboBox.class);
    	addField(ContactDto.FOLLOW_UP_UNTIL, DateField.class);

    	ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
    	contactOfficerField.setNullSelectionAllowed(true);
    	
    	setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME, ContactDto.FOLLOW_UP_UNTIL);
    		
    	setRequired(true, ContactDto.LAST_CONTACT_DATE, ContactDto.CONTACT_PROXIMITY, ContactDto.RELATION_TO_CASE);

    	addValueChangeListener(e -> {
    		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
    		updateLastContactDateValidator();
    		updateDiseaseConfiguration(caseDto.getDisease());
    		
    		// set assignable officers
    		ContactDto contactDto = getValue();
        	if (contactDto != null) {
    	    	contactOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsersByDistrict(caseDto.getDistrict(), false, UserRole.CONTACT_OFFICER));
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

	private void updateDiseaseConfiguration(Disease disease) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(SymptomsDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
