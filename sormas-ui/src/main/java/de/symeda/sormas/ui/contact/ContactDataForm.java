package de.symeda.sormas.ui.contact;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
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
		    				LayoutUtil.fluidColumnLoc(4, 0, CASE_INFO)
		    		)
		    );

	private VerticalLayout caseInfoLayout;

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
    	addField(ContactDto.DESCRIPTION, TextArea.class).setRows(3);

    	UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
    	addField(ContactDto.CONTACT_OFFICER, ComboBox.class)
    		.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.CONTACT_OFFICER));
    	
    	setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME);
    	
    	caseInfoLayout = new VerticalLayout();
    	caseInfoLayout.setSpacing(true);
    	getContent().addComponent(caseInfoLayout, CASE_INFO);
    	addValueChangeListener(e -> updateCaseInfo());
	}
    
    private void updateCaseInfo() {
    	caseInfoLayout.removeAllComponents();

    	ContactDto contactDto = getValue();
    	if (contactDto != null) {
	    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
	    	PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDto.getPerson().getUuid());

			addDescLabel(caseInfoLayout, DataHelper.getShortUuid(caseDto.getUuid()),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID))
				.setDescription(caseDto.getUuid());
			addDescLabel(caseInfoLayout, caseDto.getPerson(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON));
			
	    	HorizontalLayout ageSexLayout = new HorizontalLayout();
	    	ageSexLayout.setSpacing(true);
			addDescLabel(ageSexLayout, ApproximateAgeHelper.formatApproximateAge(
						personDto.getApproximateAge(),personDto.getApproximateAgeType()),
					I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
			addDescLabel(ageSexLayout, personDto.getSex(),
					I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
	    	caseInfoLayout.addComponent(ageSexLayout);
	    	
			addDescLabel(caseInfoLayout, caseDto.getDisease(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			addDescLabel(caseInfoLayout, caseDto.getCaseStatus(),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_STATUS));
			addDescLabel(caseInfoLayout, DateHelper.formatDMY(caseDto.getSymptoms().getOnsetDate()),
					I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
    	}
    }

	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {
		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}

	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
