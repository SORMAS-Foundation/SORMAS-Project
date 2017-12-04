package de.symeda.sormas.ui.person;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PersonCreateForm extends AbstractEditForm<PersonDto> {
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE_3, "Create new person")+
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME),
					LayoutUtil.fluidRowLocs(PersonDto.UUID, "")
					);

    public PersonCreateForm() {
        super(PersonDto.class, PersonDto.I18N_PREFIX);

        setWidth(540, Unit.PIXELS);
    }

    @Override
	protected void addFields() {
    	addField(PersonDto.UUID, TextField.class);

    	addField(PersonDto.FIRST_NAME, TextField.class);
    	addField(PersonDto.LAST_NAME, TextField.class);
    	
    	setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME);
    	setReadOnly(true, PersonDto.UUID);
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
