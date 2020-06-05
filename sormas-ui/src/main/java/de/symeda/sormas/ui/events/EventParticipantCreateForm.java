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
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class EventParticipantCreateForm extends AbstractEditForm<EventParticipantDto> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String FIRST_NAME = PersonDto.FIRST_NAME;
	private static final String LAST_NAME = PersonDto.LAST_NAME;
	
	private static final String HTML_LAYOUT =
			fluidRowLocs(EventParticipantDto.INVOLVEMENT_DESCRIPTION) +
			fluidRowLocs(FIRST_NAME, LAST_NAME);
	
	public EventParticipantCreateForm() {
		super(EventParticipantDto.class, EventParticipantDto.I18N_PREFIX);
		
        setWidth(540, Unit.PIXELS);
        
        hideValidationUntilNextCommit();
	}
	
	@Override
	protected void addFields() {
		addField(EventParticipantDto.INVOLVEMENT_DESCRIPTION, TextField.class);
		addCustomField(FIRST_NAME, String.class, TextField.class);
    	addCustomField(LAST_NAME, String.class, TextField.class);
		
		setRequired(true, EventParticipantDto.INVOLVEMENT_DESCRIPTION, FIRST_NAME, LAST_NAME);
	}
	
	public String getPersonFirstName() {
    	return (String)getField(FIRST_NAME).getValue();
    }

    public String getPersonLastName() {
    	return (String)getField(LAST_NAME).getValue();
    }
			
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
