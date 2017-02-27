package de.symeda.sormas.ui.events;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class EventGrid extends Grid {
	
	public static final String INFORMATION_SOURCE = "informationSource";
	public static final String PENDING_EVENT_TASKS = "pendingEventTasks";
	public static final String DISEASE_SHORT = "diseaseShort";

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
				return eventDto.getSrcFirstName() + " " + eventDto.getSrcLastName() + " (" + eventDto.getSrcTelNo() + ")";
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
			
		});
		
		generatedContainer.addGeneratedProperty(PENDING_EVENT_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventDto eventDto = (EventDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(eventDto.I18N_PREFIX, PENDING_EVENT_TASKS + "Format"),
						FacadeProvider.getTaskFacade().getPendingTaskCountByEvent(eventDto));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
		
		generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				EventDto eventDto = (EventDto) itemId;
				if(eventDto.getDisease() != null) {
					String diseaseName = eventDto.getDisease().getName();
					return Disease.valueOf(diseaseName).toShortString();
				} else {
					return null;
				}
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
		
		setColumns(EventDto.UUID, EventDto.EVENT_TYPE, DISEASE_SHORT, EventDto.EVENT_STATUS,
				EventDto.EVENT_DATE, EventDto.EVENT_DESC, EventDto.EVENT_LOCATION, INFORMATION_SOURCE, EventDto.REPORT_DATE_TIME, PENDING_EVENT_TASKS);
		
		getColumn(EventDto.UUID).setRenderer(new UuidRenderer());
		getColumn(EventDto.EVENT_DATE).setRenderer(new DateRenderer(DateHelper.getShortDateFormat()));
		getColumn(EventDto.REPORT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getTimeDateFormat()));
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					EventDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> ControllerProvider.getEventController().navigateToData(
				((EventDto)e.getItemId()).getUuid()));
		
		reload();
	}
	
	public void setStatusFilter(EventStatus eventStatus) {
		getContainer().removeContainerFilters(EventDto.EVENT_STATUS);
		if(eventStatus != null) {
			Equal filter = new Equal(EventDto.EVENT_STATUS, eventStatus);
			getContainer().addContainerFilter(filter);
		}
	}
	
	public void setEventTypeFilter(EventType eventType) {
		getContainer().removeContainerFilters(EventDto.EVENT_TYPE);
		if(eventType != null) {
			Equal filter = new Equal(EventDto.EVENT_TYPE, eventType);
			getContainer().addContainerFilter(filter);
		}
	}
	
	public void setDiseaseFilter(Disease disease) {
		getContainer().removeContainerFilters(EventDto.DISEASE);
		if(disease != null) {
			Equal filter = new Equal(EventDto.DISEASE, disease);
			getContainer().addContainerFilter(filter);
		}
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
	
	public void remove(EventDto event) {
		getContainer().removeItem(event);
	}

}
