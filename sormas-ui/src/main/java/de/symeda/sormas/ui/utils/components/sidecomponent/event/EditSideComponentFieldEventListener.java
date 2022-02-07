package de.symeda.sormas.ui.utils.components.sidecomponent.event;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface EditSideComponentFieldEventListener extends SerializableEventListener {

	Method ON_EDIT_SIDE_COMPONENT_FIELD_METHOD =
		ReflectTools.findMethod(EditSideComponentFieldEventListener.class, "onEditSideComponentField", EditSideComponentFieldEvent.class);

	void onEditSideComponentField(EditSideComponentFieldEvent event);
}
