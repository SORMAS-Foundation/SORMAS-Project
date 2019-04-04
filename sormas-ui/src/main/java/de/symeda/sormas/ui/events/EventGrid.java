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

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel.HasUserSelectionAllowed;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractGrid;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

@SuppressWarnings("serial")
public class EventGrid extends Grid implements AbstractGrid<EventCriteria> {
	
	public static final String INFORMATION_SOURCE = Captions.Event_informationSource;
	public static final String PENDING_EVENT_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	private EventCriteria eventCriteria = new EventCriteria();
	
	public EventGrid() {
		setSizeFull();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }
		
		BeanItemContainer<EventIndexDto> container = new BeanItemContainer<EventIndexDto>(EventIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(INFORMATION_SOURCE, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventIndexDto event = (EventIndexDto) itemId;
				return (event.getSrcFirstName() != null ? event.getSrcFirstName() : "") + " " + 
						(event.getSrcLastName() != null ? event.getSrcLastName() : "") + 
						(event.getSrcTelNo() != null && !event.getSrcTelNo().isEmpty() ? " (" + event.getSrcTelNo() + ")" : "");
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
			
		});
		
		generatedContainer.addGeneratedProperty(PENDING_EVENT_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventIndexDto event = (EventIndexDto)itemId;
				return String.format(I18nProperties.getCaption(Captions.formatSimpleNumberFormat),
						FacadeProvider.getTaskFacade().getPendingTaskCountByEvent(event.toReference()));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventIndexDto event = (EventIndexDto) itemId;
				return event.getDisease() != Disease.OTHER 
						? (event.getDisease() != null ? event.getDisease().toShortString() : "")
						: DataHelper.toStringNullable(event.getDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
		
		setColumns(EventIndexDto.UUID, EventIndexDto.EVENT_TYPE, DISEASE_SHORT, EventIndexDto.EVENT_STATUS,
				EventIndexDto.EVENT_DATE, EventIndexDto.EVENT_DESC, EventIndexDto.EVENT_LOCATION, INFORMATION_SOURCE, EventIndexDto.REPORT_DATE_TIME, PENDING_EVENT_TASKS);
		
		getColumn(EventIndexDto.UUID).setRenderer(new V7UuidRenderer());
		getColumn(EventIndexDto.EVENT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateFormat()));
		getColumn(EventIndexDto.REPORT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					EventIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> {
	       	if (e.getPropertyId() != null && (e.getPropertyId().equals(EventIndexDto.UUID) || e.isDoubleClick())) {
	       		ControllerProvider.getEventController().navigateToData(((EventIndexDto)e.getItemId()).getUuid());
	       	}
		});				
	}
	
	public void setStatusFilter(EventStatus eventStatus) {
		eventCriteria.eventStatus(eventStatus);
		reload();
	}
	
	public void setEventTypeFilter(EventType eventType) {
		eventCriteria.eventType(eventType);
		reload();
	}
	
	public void setDiseaseFilter(Disease disease) {
		eventCriteria.disease(disease);
		reload();
	}
	
    public void setReportedByFilter(UserRole reportingUserRole) {
    	eventCriteria.reportingUserRole(reportingUserRole);
    	reload();
    }
    
	@SuppressWarnings("unchecked")
	public BeanItemContainer<EventIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<EventIndexDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		if (getSelectionModel() instanceof HasUserSelectionAllowed) {
			deselectAll();
		}
		
		List<EventIndexDto> events = FacadeProvider.getEventFacade().getIndexList(UserProvider.getCurrent().getUserReference().getUuid(), eventCriteria);
		getContainer().removeAllItems();
		getContainer().addAll(events);
	}

	@Override
	public EventCriteria getCriteria() {
		return eventCriteria;
	}
	
	@Override
	public void setCriteria(EventCriteria eventCriteria) {
		this.eventCriteria = eventCriteria;
	}

	
}
