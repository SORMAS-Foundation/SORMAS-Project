package de.symeda.sormas.ui.utils.components;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class MultilineLabel extends Label {

	public MultilineLabel(String text) {
		super(text);
		setWidth(100f, Unit.PERCENTAGE);
	}

	public MultilineLabel(String text, ContentMode contentMode) {
		super(text, contentMode);
		setWidth(100f, Unit.PERCENTAGE);
	}
}
