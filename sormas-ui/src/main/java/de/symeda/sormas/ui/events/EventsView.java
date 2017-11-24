package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.user.UserRight;
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
		
    	if (LoginHelper.hasUserRight(UserRight.CREATE)) {
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
		typeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.EVENT_TYPE));
		typeFilter.addItems((Object[])EventType.values());
		typeFilter.addValueChangeListener(e -> grid.setEventTypeFilter(((EventType)e.getProperty().getValue())));
		filterLayout.addComponent(typeFilter);
		
		ComboBox diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
		filterLayout.addComponent(diseaseFilter);
		
		// TODO
//        UserDto user = LoginHelper.getCurrentUser();
//
//        ComboBox regionFilter = new ComboBox();
//        if (user.getRegion() == null) {
//            regionFilter.setWidth(140, Unit.PIXELS);
//            regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.REGION));
//            regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
//            regionFilter.addValueChangeListener(e -> {
//            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
//            	grid.setRegionFilter(region);
//            });
//            filterLayout.addComponent(regionFilter);
//        }
//
//        ComboBox districtFilter = new ComboBox();
//        districtFilter.setWidth(140, Unit.PIXELS);
//        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT));
//        districtFilter.setDescription("Select a district in the state");
//        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));
//
//        if (user.getRegion() != null) {
//            districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
//            districtFilter.setEnabled(true);
//        } else {
//            regionFilter.addValueChangeListener(e -> {
//            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
//            	districtFilter.removeAllItems();
//            	if (region != null) {
//            		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()));
//                	districtFilter.setEnabled(true);
//            	} else {
//                	districtFilter.setEnabled(false);
//            	}
//            });
//            districtFilter.setEnabled(false);
//        }
//        filterLayout.addComponent(districtFilter);
        
		return filterLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}

}
