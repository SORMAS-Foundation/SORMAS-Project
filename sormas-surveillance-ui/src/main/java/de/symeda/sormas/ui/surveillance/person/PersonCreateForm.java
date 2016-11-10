package de.symeda.sormas.ui.surveillance.person;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PersonCreateForm extends AbstractEditForm<PersonReferenceDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Create new person")+
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(PersonReferenceDto.FIRST_NAME, PersonReferenceDto.LAST_NAME),
					LayoutUtil.fluidRowLocs(PersonReferenceDto.UUID, "")
					);

    public PersonCreateForm() {
        super(PersonReferenceDto.class, PersonReferenceDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(PersonReferenceDto.UUID, TextField.class);

    	addField(PersonReferenceDto.FIRST_NAME, TextField.class);
    	addField(PersonReferenceDto.LAST_NAME, TextField.class);
    	
    	setRequired(true, PersonReferenceDto.FIRST_NAME, PersonReferenceDto.LAST_NAME);
    	setReadOnly(true, PersonReferenceDto.UUID);
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
