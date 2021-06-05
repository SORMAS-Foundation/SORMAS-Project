package de.symeda.sormas.ui.contact.components.linelisting;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface DeleteLineListener extends SerializableEventListener {

	Method DELETE_LINE_METHOD = ReflectTools.findMethod(DeleteLineListener.class, "deleteLine", DeleteLineEvent.class);

	void deleteLine(DeleteLineEvent event);
}
