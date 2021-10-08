package de.symeda.sormas.ui.utils.components.linelisting.line;

import com.vaadin.shared.Registration;
import com.vaadin.ui.HorizontalLayout;

public class LineLayout extends HorizontalLayout {

	public Registration addDeleteLineListener(DeleteLineListener deleteLineListener) {
		return addListener(DeleteLineEvent.class, deleteLineListener, DeleteLineListener.DELETE_LINE_METHOD);
	}
}
