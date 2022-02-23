package de.symeda.sormas.ui.utils.components.sidecomponent.event.sidecomponent;

import com.vaadin.ui.Component;

public class SideComponentEditEvent extends Component.Event {

	private final String uuid;

	public SideComponentEditEvent(Component source, String uuid) {
		super(source);
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}
}
