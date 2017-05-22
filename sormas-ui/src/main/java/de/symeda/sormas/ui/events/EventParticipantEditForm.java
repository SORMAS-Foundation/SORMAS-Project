package de.symeda.sormas.ui.events;

import com.vaadin.ui.TextField;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventParticipantEditForm extends AbstractEditForm<EventParticipantDto> {
	
	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(EventParticipantDto.INVOLVEMENT_DESCRIPTION) +
			LayoutUtil.fluidRowLocs(EventParticipantDto.PERSON);
	
	private final EventDto event;
	
	public EventParticipantEditForm(EventDto event) {
		super(EventParticipantDto.class, EventParticipantDto.I18N_PREFIX);
		this.event = event;
		if(event == null) {
			throw new IllegalArgumentException("Alert cannot be null");
		}
		addFields();
	}
	
	@Override
	protected void addFields() {
		if(event == null) {
			// workaround to stop initialization until event is set
			return;
		}
    	
		PersonEditForm pef = new PersonEditForm(null);
		pef.setImmediate(true);
		getFieldGroup().bind(pef, EventParticipantDto.PERSON);
		getContent().addComponent(pef, EventParticipantDto.PERSON);
		
		addField(EventParticipantDto.INVOLVEMENT_DESCRIPTION, TextField.class);
//		addField(EventParticipantDto.PERSON, PersonEditForm.class).setCaption(null);

		setRequired(true, EventParticipantDto.INVOLVEMENT_DESCRIPTION);
	}
	
    public String getPersonFirstName() {
    	return (String)getField(PersonDto.FIRST_NAME).getValue();
    }

    public String getPersonLastName() {
    	return (String)getField(PersonDto.LAST_NAME).getValue();
    }
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
