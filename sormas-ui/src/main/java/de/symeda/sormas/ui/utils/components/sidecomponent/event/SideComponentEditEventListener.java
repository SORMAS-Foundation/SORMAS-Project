package de.symeda.sormas.ui.utils.components.sidecomponent.event;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface SideComponentEditEventListener extends SerializableEventListener {

	Method ON_SIDE_COMPONENT_EDIT_METHOD = ReflectTools.findMethod(SideComponentEditEventListener.class, "onEdit", SideComponentEditEvent.class);

	void onEdit(SideComponentEditEvent event);
}
