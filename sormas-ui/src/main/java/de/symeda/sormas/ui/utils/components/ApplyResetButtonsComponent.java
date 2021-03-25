package de.symeda.sormas.ui.utils.components;

import static de.symeda.sormas.ui.utils.LayoutUtil.div;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ApplyResetButtonsComponent extends HorizontalLayout {

	private static final String FILTER_ITEM_STYLE = "filter-item";

	private static final String RESET_BUTTON_ID = "reset";
	private static final String APPLY_BUTTON_ID = "apply";

	private static final String HTML_LAYOUT = div(filterLocs(RESET_BUTTON_ID, APPLY_BUTTON_ID));

	private Button applyButton;
	private Button resetButton;

	public ApplyResetButtonsComponent() {
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(HTML_LAYOUT);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, null, FILTER_ITEM_STYLE);
		layout.addComponent(resetButton, RESET_BUTTON_ID);

		applyButton = ButtonHelper.createButton(Captions.actionApplyFilters, null, FILTER_ITEM_STYLE);
		applyButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		layout.addComponent(applyButton, APPLY_BUTTON_ID);

		addComponent(layout);
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
