package de.symeda.sormas.ui.utils.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ApplyResetButtonsComponent extends HorizontalLayout {

	private static final long serialVersionUID = 9117873923056534385L;

	private static final String FILTER_ITEM_STYLE = "filter-item";

	private final Button applyButton;
	private final Button resetButton;

	public ApplyResetButtonsComponent(String applyCaptionTag, String resetCaptionTag) {
		setSpacing(false);

		resetButton = ButtonHelper.createButton(resetCaptionTag, null, FILTER_ITEM_STYLE);
		addComponent(resetButton);

		applyButton = ButtonHelper.createButton(applyCaptionTag, null, FILTER_ITEM_STYLE);
		applyButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		addComponent(applyButton);
	}

	public void addApplyHandler(Button.ClickListener applyHandler) {
		applyButton.addClickListener(applyHandler);
	}

	public void addResetHandler(Button.ClickListener resetHandler) {
		resetButton.addClickListener(resetHandler);
	}

	public void style(String style) {
		CssStyles.style(style, applyButton, resetButton);
	}
}
