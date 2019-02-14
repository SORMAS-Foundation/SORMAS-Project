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

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CaseUuidRenderer;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EventParticipantsGrid extends Grid {
	
	public static final String PERSON_UUID = Captions.genPropPersonUuid;
	public static final String PERSON_NAME = Captions.genPropPersonName;
	public static final String PERSON_AGE = Captions.genPropPersonAge;
	public static final String PERSON_SEX = Captions.genPropPersonSex;
	public static final String CASE_ID = Captions.genPropCaseId;

	private static final String EDIT_BTN_ID = "edit";
	
	public EventParticipantsGrid() {
		setSizeFull();
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }
		
		BeanItemContainer<EventParticipantDto> container = new BeanItemContainer<EventParticipantDto>(EventParticipantDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addIconColumn(generatedContainer, EDIT_BTN_ID, FontAwesome.PENCIL_SQUARE);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(PERSON_UUID, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventParticipantDto eventParticipantDto = (EventParticipantDto)itemId;
				return eventParticipantDto.getPerson().getUuid();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(PERSON_NAME, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventParticipantDto eventParticipantDto = (EventParticipantDto)itemId;
				return eventParticipantDto.getPerson().getFirstName() + " " + eventParticipantDto.getPerson().getLastName().toUpperCase();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(PERSON_SEX, new PropertyValueGenerator<Sex>() {
			@Override
			public Sex getValue(Item item, Object itemId, Object propertyId) {
				EventParticipantDto eventParticipantDto = (EventParticipantDto)itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(eventParticipantDto.getPerson().getUuid());
				return personDto.getSex();
			}
			@Override
			public Class<Sex> getType() {
				return Sex.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(PERSON_AGE, new PropertyValueGenerator<Integer>() {
			@Override
			public Integer getValue(Item item, Object itemId, Object propertyId) {
				EventParticipantDto eventParticipantDto = (EventParticipantDto)itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(eventParticipantDto.getPerson().getUuid());
				return personDto.getApproximateAge();
			}
			@Override
			public Class<Integer> getType() {
				return Integer.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(CASE_ID, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventParticipantDto eventParticipantDto = (EventParticipantDto)itemId;
				if (eventParticipantDto.getResultingCase() != null) {
					return eventParticipantDto.getResultingCase().getUuid();
				} else {
					return "";
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		setColumns(EDIT_BTN_ID, PERSON_UUID, PERSON_NAME, PERSON_SEX, PERSON_AGE, EventParticipantDto.INVOLVEMENT_DESCRIPTION, CASE_ID);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        getColumn(EDIT_BTN_ID).setHeaderCaption("");
		getColumn(PERSON_UUID).setRenderer(new UuidRenderer());
		getColumn(CASE_ID).setRenderer(new CaseUuidRenderer(true));
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					EventParticipantDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		addItemClickListener(e -> {
			if (e.getPropertyId() == null) {
				return;
			}
			
	       	EventParticipantDto eventParticipantDto = (EventParticipantDto)e.getItemId();
	       	if(CASE_ID.equals(e.getPropertyId())) {
				if (eventParticipantDto.getResultingCase() != null) {
					ControllerProvider.getCaseController().navigateToCase(eventParticipantDto.getResultingCase().getUuid());
				} else {
					EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(eventParticipantDto.getEvent().getUuid());
					ControllerProvider.getCaseController().create(eventParticipantDto.getPerson().toReference(), eventDto.getDisease(), eventParticipantDto);
				}
	       	} else if(EDIT_BTN_ID.equals(e.getPropertyId()) || e.isDoubleClick()) {
	       		ControllerProvider.getEventParticipantController().editEventParticipant(eventParticipantDto);
	       	}
		});	
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<EventParticipantDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<EventParticipantDto>) container.getWrappedContainer();
	}
	
	public void reload(EventReferenceDto eventRef) {
		List<EventParticipantDto> entries = ControllerProvider.getEventParticipantController().getEventParticipantIndexListByEvent(eventRef);

		getContainer().removeAllItems();
		getContainer().addAll(entries);
	}
}
