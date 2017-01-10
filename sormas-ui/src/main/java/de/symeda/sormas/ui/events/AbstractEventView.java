package de.symeda.sormas.ui.events;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractEventView extends AbstractSubNavigationView {

	private EventReferenceDto eventRef;
	
	protected AbstractEventView(String viewName) {
		super(viewName);
	}
	
	@Override
	public void refreshMenu(SubNavigationMenu menu, Label itemName, Label itemUuid, String params) {
		eventRef = FacadeProvider.getEventFacade().getReferenceByUuid(params);
		
		menu.removeAllViews();
		menu.addView(EventsView.VIEW_NAME, "Events list");
		menu.addView(EventDataView.VIEW_NAME, I18nProperties.getFieldCaption(EventDto.I18N_PREFIX), params);
		itemName.setValue(eventRef.getCaption());
		itemUuid.setValue(DataHelper.getShortUuid(eventRef.getUuid()));
	}
	
	public EventReferenceDto getEventRef() {
		return eventRef;
	}

}
