package de.symeda.sormas.ui.surveillance.caze;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {
	
	private static final String PERSON_CREATE = "PersonCreate";

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Create new case")+
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(CaseDataDto.UUID),
					LayoutUtil.fluidRowLocs(CaseDataDto.DISEASE, CaseDataDto.REPORT_DATE),
					LayoutUtil.fluidRow(
							LayoutUtil.fluidColumnLoc(10, 0, CaseDataDto.PERSON),
							LayoutUtil.fluidColumnLoc(2, 0, PERSON_CREATE)),
					LayoutUtil.fluidRowLocs(CaseDataDto.HEALTH_FACILITY)
					);

    private ComboBox persons;
    
    public CaseCreateForm() {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(CaseDataDto.UUID, TextField.class);

    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	addField(CaseDataDto.REPORT_DATE, DateField.class);
    	
    	persons = addField(CaseDataDto.PERSON, ComboBox.class);
    	updatePersonsSelect();
    	
    	Button personCreateButton = new Button(null, FontAwesome.PLUS_SQUARE);
    	personCreateButton.setDescription("Create new person");
    	personCreateButton.addStyleName(ValoTheme.BUTTON_LINK);
    	personCreateButton.addStyleName(CssStyles.FORCE_CAPTION);
    	personCreateButton.addClickListener(e -> createPersonClicked());
    	getContent().addComponent(personCreateButton, PERSON_CREATE);    	

    	// TODO use only facilities from own region or district?!
    	addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class)
			.addItems(FacadeProvider.getFacilityFacade().getAllAsReference());
    	
    	setRequired(true, CaseDataDto.DISEASE, CaseDataDto.REPORT_DATE, CaseDataDto.PERSON, CaseDataDto.HEALTH_FACILITY);
    	setReadOnly(true, CaseDataDto.UUID);
    }
    
    private void createPersonClicked() {
    	ControllerProvider.getPersonController().create(
    			person ->  {
    				if (person != null) {
    					updatePersonsSelect();
    					// try to select new person
    					for (Object itemId : persons.getItemIds()) {
    						ReferenceDto dto = (ReferenceDto)itemId;
    						if (dto.getUuid().equals(person.getUuid())) {
    							persons.setValue(dto);
    							break;
    						}
						}
    				}
    			});
    }
    
    private void updatePersonsSelect() {
    	Object value = persons.getValue();
    	persons.removeAllItems();
    	persons.addItems(FacadeProvider.getPersonFacade().getAllNoCaseAsReference());
    	persons.setValue(value);
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}

}
