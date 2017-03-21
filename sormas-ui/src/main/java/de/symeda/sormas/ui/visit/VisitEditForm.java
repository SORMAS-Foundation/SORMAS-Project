package de.symeda.sormas.ui.visit;

import java.util.Date;

import com.vaadin.data.Validator;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class VisitEditForm extends AbstractEditForm<VisitDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.fluidRowLocs(VisitDto.VISIT_STATUS)+
    		LayoutUtil.fluidRowLocs(VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_REMARKS)+
			LayoutUtil.fluidRowLocs(VisitDto.SYMPTOMS)
			;
    
    private final Disease disease;
    private final ContactDto contact;

    public VisitEditForm(Disease disease, ContactDto contact) {
        super(VisitDto.class, VisitDto.I18N_PREFIX);
		this.disease = disease;
		this.contact = contact;
		if (disease == null) {
			throw new IllegalArgumentException("disease cannot be null");
		}
		addFields();
    }
    
    @Override
    protected void setInternalValue(VisitDto newValue) {
    	if (!disease.equals(newValue.getDisease())) {
    		throw new IllegalArgumentException("Visit's disease doesn't match the form configuration");
    	}
    	super.setInternalValue(newValue);
    }
    
	@Override
	protected void addFields() {
		
		if (disease == null) {
			// workaround to stop initialization until disease is set 
			return;
		}
		
    	addField(VisitDto.VISIT_DATE_TIME, DateTimeField.class);
    	addField(VisitDto.VISIT_STATUS, OptionGroup.class);
    	addField(VisitDto.VISIT_REMARKS, TextField.class);
    	
    	SymptomsForm symptomsForm = new SymptomsForm(disease);
		getFieldGroup().bind(symptomsForm, VisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, VisitDto.SYMPTOMS);
    	
    	setRequired(true, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS);
    	
    	if (contact != null) {
	    	getField(VisitDto.VISIT_DATE_TIME).addValidator(new Validator() {
	    		@Override
	    		public void validate(Object value) throws InvalidValueException {
	    			Date visitDateTime = (Date) getFieldGroup().getField(VisitDto.VISIT_DATE_TIME).getValue();
	    			if (visitDateTime.before(contact.getLastContactDate()) && DateHelper.getDaysBetween(visitDateTime, contact.getLastContactDate()) > 10) {
	    				throw new InvalidValueException("The visit cannot be more than 10 days before the last contact date.");
	    			}
	    			if (visitDateTime.after(contact.getFollowUpUntil()) && DateHelper.getDaysBetween(contact.getFollowUpUntil(), visitDateTime) > 10) {
	    				throw new InvalidValueException("The entered date is invalid. Please choose an earlier date.");
	    			}
	    		}
	    	});
    	}   	
    	
    	symptomsForm.initializeSymptomRequirementsForVisit((OptionGroup) getFieldGroup().getField(VisitDto.VISIT_STATUS));
    }
	
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
