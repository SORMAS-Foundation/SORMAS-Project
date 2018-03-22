package de.symeda.sormas.ui.events;

import java.util.Date;
import java.util.HashMap;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";
	
	private EventGrid grid;
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;
	
	private VerticalLayout gridLayout;
	
	public EventsView() {
    	super(VIEW_NAME);
    	
		grid = new EventGrid();
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		addComponent(gridLayout);
		
		if (LoginHelper.hasUserRight(UserRight.EVENT_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid, "sormas_events", "sormas_events_" + DateHelper.formatDateForExport(new Date()) + ".csv", "text/csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
			
			addHeaderComponent(exportButton);
		}
		
    	if (LoginHelper.hasUserRight(UserRight.EVENT_CREATE)) {
			createButton = new Button("New event");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getEventController().create());
			addHeaderComponent(createButton);
    	}
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		
		ComboBox typeFilter = new ComboBox();
		typeFilter.setWidth(140, Unit.PIXELS);
		typeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.EVENT_TYPE));
		typeFilter.addItems((Object[])EventType.values());
		typeFilter.addValueChangeListener(e -> {
			grid.setEventTypeFilter(((EventType)e.getProperty().getValue()));
			updateActiveStatusButtonCaption();
		});
		filterLayout.addComponent(typeFilter);
		
		ComboBox diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			grid.setDiseaseFilter(((Disease)e.getProperty().getValue()));
			updateActiveStatusButtonCaption();
		});
		filterLayout.addComponent(diseaseFilter);
        
        ComboBox reportedByFilter = new ComboBox();
        reportedByFilter.setWidth(140, Unit.PIXELS);
        reportedByFilter.setInputPrompt("Reported By");
        reportedByFilter.addItems((Object[]) UserRole.values());
        reportedByFilter.addValueChangeListener(e -> {
        	grid.setReportedByFilter((UserRole) e.getProperty().getValue());
			updateActiveStatusButtonCaption();
        });
        filterLayout.addComponent(reportedByFilter);
        
		return filterLayout;
	}
	
	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setSizeUndefined();
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();
		
		Button statusAll = new Button("All", e -> processStatusChange(null, e.getButton()));
		CssStyles.style(statusAll, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, "All");
		
		for(EventStatus status : EventStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> processStatusChange(status, e.getButton()));
			CssStyles.style(statusButton, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			statusButton.setCaptionAsHtml(true);
			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}
		
		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	private void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + "<span class=\"" + CssStyles.BADGE + "\">" + grid.getContainer().size() + "</span>");
		}
	}
	
	private void processStatusChange(EventStatus eventStatus, Button button) {
		grid.setStatusFilter(eventStatus);
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
		updateActiveStatusButtonCaption();
	}

}
