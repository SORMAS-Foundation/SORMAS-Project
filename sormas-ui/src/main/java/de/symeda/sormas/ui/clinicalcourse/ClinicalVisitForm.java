package de.symeda.sormas.ui.clinicalcourse;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ClinicalVisitForm extends AbstractEditForm<ClinicalVisitDto> {

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(ClinicalVisitDto.VISIT_DATE_TIME, ClinicalVisitDto.VISITING_PERSON) +
			LayoutUtil.loc(ClinicalVisitDto.VISIT_REMARKS) +
			LayoutUtil.fluidRowLocs(ClinicalVisitDto.SYMPTOMS);

    private final Disease disease;
    private final PersonDto person;
    private SymptomsForm symptomsForm;
    
	public ClinicalVisitForm(boolean create, Disease disease, PersonDto person, UserRight editOrCreateUserRight) {
		super(ClinicalVisitDto.class, ClinicalVisitDto.I18N_PREFIX, editOrCreateUserRight);
		if (create) {
			hideValidationUntilNextCommit();
		}
		if (disease == null) {
			throw new IllegalArgumentException("Disease cannot be null");
		}
		this.disease = disease;
		this.person = person;
		addFields();
	}

    @Override
    protected void setInternalValue(ClinicalVisitDto newValue) {
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
    	
    	addField(ClinicalVisitDto.VISIT_DATE_TIME, DateTimeField.class);
    	addField(ClinicalVisitDto.VISITING_PERSON, TextField.class);
    	addField(ClinicalVisitDto.VISIT_REMARKS, TextField.class);
    	
    	symptomsForm = new SymptomsForm(null, disease, person, SymptomsContext.CLINICAL_VISIT, UserRight.CLINICAL_VISIT_EDIT, null);
    	getFieldGroup().bind(symptomsForm, ClinicalVisitDto.SYMPTOMS);
    	getContent().addComponent(symptomsForm, ClinicalVisitDto.SYMPTOMS);
    	
    	setRequired(true, ClinicalVisitDto.VISIT_DATE_TIME);	
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
	
}
