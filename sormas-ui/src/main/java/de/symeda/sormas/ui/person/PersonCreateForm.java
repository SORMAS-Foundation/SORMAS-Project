/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class PersonCreateForm extends AbstractEditForm<PersonDto> {
		
		private static final long serialVersionUID = 1L;
		
	
    private static final String HTML_LAYOUT = 
    		h3(I18nProperties.getString(Strings.headingCreateNewPerson)) +
			fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
			fluidRowLocs(PersonDto.UUID, "");

    public PersonCreateForm(UserRight editOrCreateUserRight) {
    	// TODO add user right parameter
        super(PersonDto.class, PersonDto.I18N_PREFIX, editOrCreateUserRight);

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
