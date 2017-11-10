package de.symeda.sormas.ui.contact;

import java.util.Arrays;

import org.joda.time.LocalDate;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Link;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class ContactDataForm extends AbstractEditForm<ContactDto> {
	
	private static final String TO_CASE_BTN_LOC = "toCaseBtnLoc";
	private static final String CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC = "cancelOrResumeFollowUpBtnLoc";
	private static final String LOST_FOLLOW_UP_BTN_LOC = "lostFollowUpBtnLoc";
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE_3, "Contact data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE_2, 
		    		LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_CLASSIFICATION) +
		    						LayoutUtil.locCss(CssStyles.VSPACE_3, TO_CASE_BTN_LOC) +
		    						LayoutUtil.fluidRowLocs(ContactDto.LAST_CONTACT_DATE, ContactDto.UUID) +
		    						LayoutUtil.fluidRowLocs(ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME) +
		    						LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY, "") +
		    						LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
				    				LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION) +
				    				LayoutUtil.h3(CssStyles.VSPACE_3, "Follow-up status") +
				    				LayoutUtil.fluidRowLocs(ContactDto.FOLLOW_UP_STATUS, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC, LOST_FOLLOW_UP_BTN_LOC) +
				    				LayoutUtil.fluidRowLocs(ContactDto.FOLLOW_UP_COMMENT) +
				    				LayoutUtil.fluidRowLocs(ContactDto.FOLLOW_UP_UNTIL, ContactDto.CONTACT_OFFICER)
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
    	DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
    	addField(ContactDto.REPORT_DATE_TIME, DateField.class);
    	OptionGroup contactProximity = addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
    	contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
    	ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
    	addField(ContactDto.DESCRIPTION, TextArea.class).setRows(3);

    	addField(ContactDto.FOLLOW_UP_STATUS, ComboBox.class);
    	addField(ContactDto.FOLLOW_UP_COMMENT, TextArea.class).setRows(1);
    	addField(ContactDto.FOLLOW_UP_UNTIL, DateField.class);

    	ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
    	contactOfficerField.setNullSelectionAllowed(true);
    	
    	setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME, 
    			ContactDto.FOLLOW_UP_STATUS, ContactDto.FOLLOW_UP_UNTIL);
    	
    	FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.FOLLOW_UP_STATUS, 
    			Arrays.asList(ContactDto.FOLLOW_UP_COMMENT), 
    			Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.LOST));

    	addValueChangeListener(e -> {
        	if (getValue() != null) {
	    		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
	    		updateLastContactDateValidator();
	    		updateDiseaseConfiguration(caseDto.getDisease());
	    		
	    		updateFollowUpStatusComponents();
    		
    	    	contactOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsersByDistrict(caseDto.getDistrict(), false, UserRole.CONTACT_OFFICER));
    	    	
    	    	String associatedCaseUuid = findAssociatedCaseUuid(FacadeProvider.getPersonFacade().getPersonByUuid(getValue().getPerson().getUuid()), getValue());
    	    	getContent().removeComponent(TO_CASE_BTN_LOC);
    	    	if (associatedCaseUuid == null) {
    	    		if (LoginHelper.hasUserRight(UserRight.CREATE)) {
	    		    	Button toCaseButton = new Button("Create a case for this contact person");
	    				toCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
	    				
	    				toCaseButton.addClickListener(new ClickListener() {
	    					@Override
	    					public void buttonClick(ClickEvent event) {
	    						PersonReferenceDto personRef = getValue().getPerson();
	    						PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(personRef.getUuid());
	    						CaseReferenceDto caseRef = getValue().getCaze();
	    						CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
	    						ControllerProvider.getCaseController().create(person, caze.getDisease(), getValue());
	    					}
	    				});
	    				
	    				getContent().addComponent(toCaseButton, TO_CASE_BTN_LOC);
    	    		}
    	    	} else {
    	    		// link to case
    		    	Link linkToData = ControllerProvider.getCaseController().createLinkToData(associatedCaseUuid, "Open case of this contact person");
    		    	getContent().addComponent(linkToData, TO_CASE_BTN_LOC);
    	    	}
        	}
    	});
    	
    	FieldHelper.makeFieldSoftRequired(lastContactDate, contactProximity, relationToCase);
	}
    
    private void updateFollowUpStatusComponents() {

		getContent().removeComponent(CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
		getContent().removeComponent(LOST_FOLLOW_UP_BTN_LOC);

		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
		boolean followUpVisible = getValue() != null && statusField.isVisible();
		if (followUpVisible && LoginHelper.hasUserRight(UserRight.EDIT)) {
			FollowUpStatus followUpStatus = statusField.getValue();
			if (followUpStatus == FollowUpStatus.FOLLOW_UP) {
				
		    	Button cancelButton = new Button(I18nProperties.getFragment("Contact.cancelFollowUp"));
		    	cancelButton.setWidth(100, Unit.PERCENTAGE);
		    	cancelButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.CANCELED);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(cancelButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);

		    	Button lostButton = new Button(I18nProperties.getFragment("Contact.lostToFollowUp"));
		    	lostButton.setWidth(100, Unit.PERCENTAGE);
		    	lostButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.LOST);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(lostButton, LOST_FOLLOW_UP_BTN_LOC);
				
			} else if (followUpStatus == FollowUpStatus.CANCELED
					|| followUpStatus == FollowUpStatus.LOST) {

		    	Button resumeButton = new Button(I18nProperties.getFragment("Contact.resumeFollowUp"));
		    	resumeButton.addStyleName(CssStyles.FORCE_CAPTION);
		    	resumeButton.setWidth(100, Unit.PERCENTAGE);
		    	resumeButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.FOLLOW_UP);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(resumeButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
			}
		}		
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
	    			null, new LocalDate(getValue().getReportDateTime()).plusDays(1).toDate(), Resolution.SECOND));
    	}
    }

	private void updateDiseaseConfiguration(Disease disease) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(ContactDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
	
	private String findAssociatedCaseUuid(PersonDto personDto, ContactDto contactDto) {
		if(personDto == null || contactDto == null) {
			return null;
		}
		
		UserDto user = LoginHelper.getCurrentUser();
		CaseDataDto contactCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
		CaseDataDto caze = FacadeProvider.getCaseFacade().getByPersonAndDisease(personDto.getUuid(), contactCase.getDisease(), user.getUuid());
		if(caze != null) {
			return caze.getUuid();
		} else {
			return null;
		}
	}
}
