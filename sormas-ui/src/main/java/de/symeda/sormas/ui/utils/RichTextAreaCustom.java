package de.symeda.sormas.ui.utils;

import com.vaadin.event.FieldEvents;
import com.vaadin.v7.ui.RichTextArea;

public class RichTextAreaCustom extends RichTextArea {

	public void addFocusListener(FieldEvents.FocusListener listener) {
		addListener(FieldEvents.FocusEvent.EVENT_ID, FieldEvents.FocusEvent.class, listener, FieldEvents.FocusListener.focusMethod);
	}

	public void removeFocusListener(FieldEvents.FocusListener listener) {
		removeListener(FieldEvents.FocusEvent.EVENT_ID, FieldEvents.FocusEvent.class, listener);

	}

	public void addBlurListener(FieldEvents.BlurListener listener) {
		addListener(FieldEvents.BlurEvent.EVENT_ID, FieldEvents.BlurEvent.class, listener, FieldEvents.BlurListener.blurMethod);
	}

	public void removeBlurListener(FieldEvents.BlurListener listener) {
		removeListener(FieldEvents.BlurEvent.EVENT_ID, FieldEvents.BlurEvent.class, listener);
	}
}
