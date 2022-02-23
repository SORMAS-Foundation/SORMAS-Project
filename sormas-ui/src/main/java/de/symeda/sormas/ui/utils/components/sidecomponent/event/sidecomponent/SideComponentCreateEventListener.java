package de.symeda.sormas.ui.utils.components.sidecomponent.event.sidecomponent;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface SideComponentCreateEventListener extends SerializableEventListener {

	Method ON_SIDE_COMPONENT_CREATE_METHOD =
		ReflectTools.findMethod(SideComponentCreateEventListener.class, "onCreate", SideComponentCreateEvent.class);

	void onCreate(SideComponentCreateEvent event);
}
