package de.symeda.sormas.ui.events;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class EventGrid extends Grid {
	
	public static final String INFORMATION_SOURCE = "informationSource";
	public static final String PENDING_TASKS = "pendingTasks";
	
	public EventGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<EventDto> container = new BeanItemContainer<EventDto>(EventDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		generatedContainer.addGeneratedProperty(INFORMATION_SOURCE, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventDto eventDto = (EventDto)itemId;
				return eventDto.getSrcFirstName() + " " + eventDto.getSrcLastName() + " " + eventDto.getUuid();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
			
		});
		
		generatedContainer.addGeneratedProperty(PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventDto eventDto = (EventDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(eventDto.I18N_PREFIX, PENDING_TASKS + "Format"),
						FacadeProvider.getTaskFacade().getPendingTaskCountByEvent(eventDto));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		setColumns(EventDto.UUID, EventDto.EVENT_TYPE, EventDto.EVENT_STATUS,
				EventDto.EVENT_DESC, EventDto.EVENT_LOCATION, INFORMATION_SOURCE, EventDto.REPORT_DATE_TIME,
				PENDING_TASKS);
		
		getColumn(EventDto.UUID).setRenderer(new UuidRenderer());
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					EventDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> ControllerProvider.getEventController().navigateToData(
				((EventDto)e.getItemId()).getUuid()));
		
		reload();
	}
	
	public void setStatusFilter(EventStatus eventStatus) {
		
	}
	
	public void setEventTypeFilter(EventType eventType) {
		
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<EventDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<EventDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		List<EventDto> events = ControllerProvider.getEventController().getEventIndexList();
		getContainer().removeAllItems();
		getContainer().addAll(events);
	}
	
	public void refresh(EventDto event) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
		BeanItem<EventDto> item = getContainer().getItem(event);
		if(item != null) {
            // Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(EventDto.UUID);
			p.fireValueChange();
		} else {
            // New product
			getContainer().addBean(event);
		}
	}

}
