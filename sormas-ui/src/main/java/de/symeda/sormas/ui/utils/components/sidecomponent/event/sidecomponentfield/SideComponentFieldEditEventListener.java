package de.symeda.sormas.ui.utils.components.sidecomponent.event.sidecomponentfield;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface SideComponentFieldEditEventListener extends SerializableEventListener {

	Method ON_EDIT_SIDE_COMPONENT_FIELD_METHOD =
		ReflectTools.findMethod(SideComponentFieldEditEventListener.class, "onEditSideComponentField", SideComponentFieldEditEvent.class);

	void onEditSideComponentField(SideComponentFieldEditEvent event);
}
