package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventParticipantsView extends AbstractEventView {
	
	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "events/eventparticipants";
	
	private EventParticipantsGrid grid;
	private Button addButton;
	private VerticalLayout gridLayout;
	
	public EventParticipantsView() {
		super(VIEW_NAME);
		
		setSizeFull();
		addStyleName("crud-view");
		
		grid = new EventParticipantsGrid();
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		setSubComponent(gridLayout);
	}
	
	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth("100%");
		
		Label header = new Label(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.EVENT_PERSONS));
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
		topLayout.addComponent(header);
		
		addButton = new Button("Add person");
		addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		addButton.setIcon(FontAwesome.PLUS_CIRCLE);
		addButton.addClickListener(e -> {
			ControllerProvider.getEventParticipantController().createEventParticipant(this.getEventRef(),
					r -> grid.reload(getEventRef()));
		});
		topLayout.addComponent(addButton);
		topLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
		topLayout.setExpandRatio(addButton, 1);
		
		topLayout.addStyleName(CssStyles.VSPACE3);
		return topLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(event != null) {
			super.enter(event);
		}
		grid.reload(getEventRef());
	}

}
