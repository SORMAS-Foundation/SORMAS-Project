package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Lightweight, type-safe event bus scoped to a single form instance.
 * Components fire and listen to typed events without direct references to each other.
 */
public class FormEventBus {

	private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

	public <E> com.vaadin.shared.Registration on(Class<E> eventType, Consumer<E> handler) {
		listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
		return () -> {
			List<Consumer<?>> list = listeners.get(eventType);
			if (list != null) {
				list.remove(handler);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <E> void fire(E event) {
		List<Consumer<?>> list = listeners.get(event.getClass());
		if (list != null) {
			for (Consumer<?> handler : new ArrayList<>(list)) {
				((Consumer<E>) handler).accept(event);
			}
		}
	}
}
