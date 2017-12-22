package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";
	
	private EventGrid grid;
	private Button createButton;
	
	private VerticalLayout gridLayout;
	
	public EventsView() {
    	super(VIEW_NAME);
    	
		grid = new EventGrid();
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBar());
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		addComponent(gridLayout);
		
    	if (LoginHelper.hasUserRight(UserRight.EVENT_CREATE)) {
			createButton = new Button("New event");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getEventController().create());
			addHeaderComponent(createButton);
    	}
	}
	
	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setSizeUndefined();
		topLayout.addStyleName(CssStyles.VSPACE_3);
		
		Button statusAll = new Button("all", e -> grid.setStatusFilter(null));
		statusAll.setStyleName(ValoTheme.BUTTON_LINK);
		topLayout.addComponent(statusAll);
		
		for(EventStatus status : EventStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> grid.setStatusFilter(status));
			statusButton.setStyleName(ValoTheme.BUTTON_LINK);
			topLayout.addComponent(statusButton);
		}
		
		return topLayout;
	}
	
	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		
		ComboBox typeFilter = new ComboBox();
		typeFilter.setWidth(140, Unit.PIXELS);
		typeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.EVENT_TYPE));
		typeFilter.addItems((Object[])EventType.values());
		typeFilter.addValueChangeListener(e -> grid.setEventTypeFilter(((EventType)e.getProperty().getValue())));
		filterLayout.addComponent(typeFilter);
		
		ComboBox diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
		filterLayout.addComponent(diseaseFilter);
        
        ComboBox reportedByFilter = new ComboBox();
        reportedByFilter.setWidth(140, Unit.PIXELS);
        reportedByFilter.setInputPrompt("Reported By");
        reportedByFilter.addItems((Object[]) UserRole.values());
        reportedByFilter.addValueChangeListener(e -> {
        	grid.setReportedByFilter((UserRole) e.getProperty().getValue());
        });
        filterLayout.addComponent(reportedByFilter);
        
		return filterLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}

}
