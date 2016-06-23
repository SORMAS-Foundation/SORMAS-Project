package de.symeda.sormas.ui.surveillance.caze;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class PatientInformationForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;
    
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Patient information")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.FIRST_NAME)),
						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.LAST_NAME))
//						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(CaseDto.DESCRIPTION))
					)
				);

    public PatientInformationForm() {
    	super(PersonDto.class);
    }

    @Override
	protected void addFields() {
		Map<String, Class<? extends Field<?>>> formProperties = new HashMap<String, Class<? extends Field<?>>>();
		formProperties.put(PersonDto.FIRST_NAME, TextField.class);
		formProperties.put(PersonDto.LAST_NAME, TextField.class);
		
		// @TODO: put this in i18n properties
		Map<String, String> captions = new HashMap<String, String>();
		captions.put(PersonDto.FIRST_NAME, "First name");
		captions.put(PersonDto.LAST_NAME, "Last name");
		

		for (String propertyId : formProperties.keySet()) {
			Field<?> field = getFieldGroup().buildAndBind(captions.get(propertyId), propertyId, formProperties.get(propertyId));
			field.setReadOnly(true);
			field.setSizeFull();
	        addComponent(field, propertyId);
		}
	}

	@Override
	protected void setLayout() {
		setTemplateContents(HTML_LAYOUT);
	}
    
}
