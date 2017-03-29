package de.symeda.sormas.ui.events;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventParticipantCreateForm extends AbstractEditForm<EventParticipantDto> {
	
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	
	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(EventParticipantDto.INVOLVEMENT_DESCRIPTION) +
			LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME);
	
	public EventParticipantCreateForm() {
		super(EventParticipantDto.class, EventParticipantDto.I18N_PREFIX);
		
        setWidth(540, Unit.PIXELS);
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
