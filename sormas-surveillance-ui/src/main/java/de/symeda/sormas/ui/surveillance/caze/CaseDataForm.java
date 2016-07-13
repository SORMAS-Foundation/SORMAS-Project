package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {
	
	private static final String CHANGE_TO_SUSPECT = "changeToSuspect";
	private static final String CHANGE_TO_NONCASE = "changeToNoncase";

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE2, 
		    		LayoutUtil.fluidRowLocs(CaseDataDto.UUID, CaseDataDto.CASE_STATUS, CHANGE_TO_SUSPECT),
		    		LayoutUtil.fluidRowLocs(CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE, CHANGE_TO_NONCASE),
		    		LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDataDto.DISEASE)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDataDto.HEALTH_FACILITY))
					)
		    )+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Responsible users")+
    		LayoutUtil.divCss(CssStyles.VSPACE2, 
    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.CASE_OFFICER, CaseDataDto.CONTACT_OFFICER),
    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_SUPERVISOR, CaseDataDto.CASE_SUPERVISOR, CaseDataDto.CONTACT_SUPERVISOR)
			);
    

    public CaseDataForm() {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(CaseDataDto.UUID, TextField.class);
    	addField(CaseDataDto.CASE_STATUS, NativeSelect.class);
    	addField(CaseDataDto.REPORTING_USER, ComboBox.class);
    	addField(CaseDataDto.REPORT_DATE, DateField.class);
    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
    	// TODO use only facilities from own region or district?!
    	addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class)
			.addItems(FacadeProvider.getFacilityFacade().getAllAsReference());
    	
    	// TODO use only users from own region or district?!
    	addField(CaseDataDto.SURVEILLANCE_SUPERVISOR, ComboBox.class)
			.addItems(FacadeProvider.getUserFacade().getListAsReference(UserRole.SURVEILLANCE_SUPERVISOR));
    	addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class)
			.addItems(FacadeProvider.getUserFacade().getListAsReference(UserRole.SURVEILLANCE_OFFICER));

    	addField(CaseDataDto.CASE_SUPERVISOR, ComboBox.class);
    	addField(CaseDataDto.CASE_OFFICER, ComboBox.class);
    	addField(CaseDataDto.CONTACT_SUPERVISOR, ComboBox.class);
    	addField(CaseDataDto.CONTACT_OFFICER, ComboBox.class);
    	
    	setRequired(true, CaseDataDto.HEALTH_FACILITY);
    	setReadOnly(true, CaseDataDto.UUID, 
    			CaseDataDto.CASE_STATUS, CaseDataDto.DISEASE, 
    			CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE, 
    			CaseDataDto.CASE_SUPERVISOR, CaseDataDto.CASE_OFFICER, 
    			CaseDataDto.CONTACT_SUPERVISOR, CaseDataDto.CONTACT_OFFICER);

    	// TODO show these buttons only for chosen userroles
    	addComponent(createButton(CHANGE_TO_SUSPECT, e -> setCaseStatus(CaseStatus.SUSPECT)), CHANGE_TO_SUSPECT);   
    	addComponent(createButton(CHANGE_TO_NONCASE, e -> setCaseStatus(CaseStatus.NO_CASE)), CHANGE_TO_NONCASE);   
	}
    
    private Button createButton(String propertyId, ClickListener listener) {
    	Button button = new Button();
    	button.setCaption(I18nProperties.getButtonCaption(getPropertyI18nPrefix()+"."+propertyId, propertyId));
    	button.addStyleName(ValoTheme.BUTTON_PRIMARY);
    	button.addStyleName(CssStyles.FORCE_CAPTION);
    	button.setWidth(100, Unit.PERCENTAGE);
    	button.addClickListener(listener);
    	return button;
    }
    
	private void setCaseStatus(CaseStatus status) {
		NativeSelect field = (NativeSelect)getFieldGroup().getField(CaseDataDto.CASE_STATUS);
		field.setReadOnly(false);
		field.setValue(status);
		field.setReadOnly(true);
	}

	@Override
	protected void setLayout() {
		 setTemplateContents(HTML_LAYOUT);
	}

}
