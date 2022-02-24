package de.symeda.sormas.ui.utils.components.sidecomponent.event;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface SideComponentFieldEditEventListener extends SerializableEventListener {

	Method ON_SIDE_COMPONENT_FIELD_EDIT_METHOD =
		ReflectTools.findMethod(SideComponentFieldEditEventListener.class, "onEdit", SideComponentFieldEditEvent.class);

	void onEdit(SideComponentFieldEditEvent event);
}
