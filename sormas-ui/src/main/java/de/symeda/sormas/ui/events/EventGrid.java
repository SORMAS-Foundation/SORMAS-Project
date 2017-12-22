package de.symeda.sormas.ui.events;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class EventGrid extends Grid {
	
	public static final String INFORMATION_SOURCE = "informationSource";
	public static final String PENDING_EVENT_TASKS = "pendingEventTasks";
	public static final String DISEASE_SHORT = "diseaseShort";

	private final EventCriteria eventCriteria = new EventCriteria();
	
	public EventGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		
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
				return String.format(I18nProperties.getPrefixFieldCaption(EventIndexDto.I18N_PREFIX, PENDING_EVENT_TASKS + "Format"),
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
		
		getColumn(EventIndexDto.UUID).setRenderer(new UuidRenderer());
		getColumn(EventIndexDto.EVENT_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		getColumn(EventIndexDto.REPORT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getDateTimeFormat()));
		
		for(Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					EventIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		addItemClickListener(e -> ControllerProvider.getEventController().navigateToData(
				((EventIndexDto)e.getItemId()).getUuid()));
	}
	
	public void setStatusFilter(EventStatus eventStatus) {
		eventCriteria.eventStatusEquals(eventStatus);
		reload();
	}
	
	public void setEventTypeFilter(EventType eventType) {
		eventCriteria.eventTypeEquals(eventType);
		reload();
	}
	
	public void setDiseaseFilter(Disease disease) {
		eventCriteria.diseaseEquals(disease);
		reload();
	}
	
    public void setReportedByFilter(UserRole reportingUserRole) {
    	eventCriteria.reportingUserHasRole(reportingUserRole);
    	reload();
    }
    
	@SuppressWarnings("unchecked")
	private BeanItemContainer<EventIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<EventIndexDto>) container.getWrappedContainer();
	}
	
	public void reload() {
		List<EventIndexDto> events = FacadeProvider.getEventFacade().getIndexList(LoginHelper.getCurrentUserAsReference().getUuid(), eventCriteria);
		getContainer().removeAllItems();
		getContainer().addAll(events);
	}
}
