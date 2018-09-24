package de.symeda.sormas.ui.visit;

import java.util.Date;

import com.vaadin.data.Validator;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.user.UserRight;
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
    private final PersonDto person;
    private SymptomsForm symptomsForm;

    public VisitEditForm(Disease disease, ContactDto contact, PersonDto person, boolean create, UserRight editOrCreateUserRight) {
        super(VisitDto.class, VisitDto.I18N_PREFIX, editOrCreateUserRight);
        if (create) {
        	hideValidationUntilNextCommit();
        }
		this.disease = disease;
		this.contact = contact;
		this.person = person;
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
    	
    	symptomsForm = new SymptomsForm(disease, person, SymptomsContext.VISIT, UserRight.VISIT_EDIT, null);
		getFieldGroup().bind(symptomsForm, VisitDto.SYMPTOMS);
		getContent().addComponent(symptomsForm, VisitDto.SYMPTOMS);
    	
    	setRequired(true, VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS);
    	
    	if (contact != null) {
	    	getField(VisitDto.VISIT_DATE_TIME).addValidator(new Validator() {
	    		@Override
	    		public void validate(Object value) throws InvalidValueException {
	    			Date visitDateTime = (Date) getFieldGroup().getField(VisitDto.VISIT_DATE_TIME).getValue();
	    			Date contactReferenceDate = contact.getLastContactDate() != null ? contact.getLastContactDate() : contact.getReportDateTime();
	    			if (visitDateTime.before(contactReferenceDate) && DateHelper.getDaysBetween(visitDateTime, contactReferenceDate) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
	    				throw new InvalidValueException("The visit cannot be more than " + VisitDto.ALLOWED_CONTACT_DATE_OFFSET + " days before the " + (contact.getLastContactDate() != null ? "last contact date." : "contact report date."));
	    			}
	    			if (contact.getFollowUpUntil() != null && visitDateTime.after(contact.getFollowUpUntil()) && DateHelper.getDaysBetween(contact.getFollowUpUntil(), visitDateTime) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
	    				throw new InvalidValueException("The visit cannot be more than " + VisitDto.ALLOWED_CONTACT_DATE_OFFSET + " days after the end of the follow-up duration.");
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

	public SymptomsForm getSymptomsForm() {
		return symptomsForm;
	}
	
}
