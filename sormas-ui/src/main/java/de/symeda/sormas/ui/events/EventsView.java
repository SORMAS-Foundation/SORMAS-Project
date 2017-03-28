package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";
	
	private EventGrid grid;
	private Button createButton;
	
	private VerticalLayout gridLayout;
	
	public EventsView() {
		setSizeFull();
		addStyleName("crud-view");
		
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
	}
	
	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE3);
		
		Label header = new Label("Alerts");
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
		topLayout.addComponent(header);
		
		Button statusAll = new Button("all", e -> grid.setStatusFilter(null));
		statusAll.setStyleName(ValoTheme.BUTTON_LINK);
		topLayout.addComponent(statusAll);
		
		for(EventStatus status : EventStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> grid.setStatusFilter(status));
			statusButton.setStyleName(ValoTheme.BUTTON_LINK);
			topLayout.addComponent(statusButton);
		}
		
		createButton = new Button("New alert");
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(FontAwesome.PLUS_CIRCLE);
		createButton.addClickListener(e -> ControllerProvider.getEventController().create());
		topLayout.addComponent(createButton);
		topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		topLayout.setExpandRatio(createButton, 1);
		
		return topLayout;
	}
	
	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE3);
		
		ComboBox typeFilter = new ComboBox();
		typeFilter.setWidth(200, Unit.PIXELS);
		typeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.EVENT_TYPE));
		typeFilter.addItems((Object[])EventType.values());
		typeFilter.addValueChangeListener(e -> grid.setEventTypeFilter(((EventType)e.getProperty().getValue())));
		filterLayout.addComponent(typeFilter);
		
		ComboBox diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
		filterLayout.addComponent(diseaseFilter);
		
		return filterLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}

}
