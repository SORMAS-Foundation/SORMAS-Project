package de.symeda.sormas.ui.visit;

import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
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

    public VisitEditForm(Disease disease) {
        super(VisitDto.class, VisitDto.I18N_PREFIX);
		this.disease = disease;
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
    	symptomsForm.initializeSymptomRequirementsForVisit(getFieldGroup().getField(VisitDto.VISIT_STATUS));
    }
	
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
