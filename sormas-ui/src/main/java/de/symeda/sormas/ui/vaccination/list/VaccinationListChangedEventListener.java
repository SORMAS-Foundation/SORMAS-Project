package de.symeda.sormas.ui.vaccination.list;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;
import com.vaadin.util.ReflectTools;

@FunctionalInterface
public interface VaccinationListChangedEventListener extends SerializableEventListener {

	Method ON_CHANGE_METHOD = ReflectTools.findMethod(VaccinationListChangedEventListener.class, "onChange", VaccinationListChangedEvent.class);

	void onChange(VaccinationListChangedEvent event);
}
