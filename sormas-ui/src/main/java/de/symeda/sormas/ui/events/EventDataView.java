package de.symeda.sormas.ui.events;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.ui.ControllerProvider;

public class EventDataView extends AbstractEventView {
	
	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "events/data";
	
	public EventDataView() {
		super(VIEW_NAME);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setSubComponent(ControllerProvider.getEventController().getEventDataEditComponent(getEventRef().getUuid()));
	}

}
