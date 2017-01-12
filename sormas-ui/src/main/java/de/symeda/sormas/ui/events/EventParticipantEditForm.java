package de.symeda.sormas.ui.events;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventParticipantEditForm extends AbstractEditForm<EventParticipantDto> {

	public static final String PERSON_CREATE = "PersonCreate";
	
	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(EventParticipantDto.KIND_OF_INVOLVEMENT) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumnLoc(6, 0, EventParticipantDto.PERSON),
					LayoutUtil.fluidColumnLoc(2, 0, PERSON_CREATE)
			);
	
	private final EventDto event;
	private ComboBox persons;
	
	public EventParticipantEditForm(EventDto event) {
		super(EventParticipantDto.class, EventParticipantDto.I18N_PREFIX);
		this.event = event;
		if(event == null) {
			throw new IllegalArgumentException("event cannot be null");
		}
		addFields();
	}
	
	@Override
	protected void addFields() {
		if(event == null) {
			// workaround to stop initialization until event is set
			return;
		}
		
		addField(EventParticipantDto.KIND_OF_INVOLVEMENT, OptionGroup.class);
		
		persons = addField(EventParticipantDto.PERSON, ComboBox.class);
		updatePersonsSelect();
		
		Button personCreateButton = new Button(null, FontAwesome.PLUS_SQUARE);
		personCreateButton.setDescription("Create new person");
		personCreateButton.addStyleName(ValoTheme.BUTTON_LINK);
		personCreateButton.addStyleName(CssStyles.FORCE_CAPTION);
		personCreateButton.addClickListener(e -> createPersonClicked());
		getContent().addComponent(personCreateButton, PERSON_CREATE);
		
		setRequired(true, EventParticipantDto.KIND_OF_INVOLVEMENT, EventParticipantDto.PERSON);
	}
	
	private void createPersonClicked() {
		ControllerProvider.getPersonController().create(
			person -> {
				if(person != null) {
					updatePersonsSelect();
					for(Object itemId : persons.getItemIds()) {
						PersonReferenceDto dto = (PersonReferenceDto)itemId;
						if(dto.getUuid().equals(person.getUuid())) {
							persons.setValue(dto);
							break;
						}
					}
				}
			}
		);
	}
	
	private void updatePersonsSelect() {
		Object value = persons.getValue();
		persons.removeAllItems();
		persons.addItems(FacadeProvider.getPersonFacade().getAllPersons());
		persons.setValue(value);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
