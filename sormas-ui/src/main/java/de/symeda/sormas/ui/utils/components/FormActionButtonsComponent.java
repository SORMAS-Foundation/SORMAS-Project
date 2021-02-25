package de.symeda.sormas.ui.utils.components;

import static de.symeda.sormas.ui.utils.LayoutUtil.div;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.HorizontalLayout;

public class FormActionButtonsComponent extends HorizontalLayout {

	private static final String APPLY_RESET_BUTTON_ID = "apply-reset";
	private static final String EXPAND_COLLAPSE_ID = "expandCollapse";

	private static final String HTML_LAYOUT = div(filterLocs(EXPAND_COLLAPSE_ID, APPLY_RESET_BUTTON_ID));

	private ToggleMoreFiltersComponent showHideMoreButton;
	private ApplyResetButtonsComponent applyResetButtonsComponent;

	public FormActionButtonsComponent(CustomLayout moreFiltersLayout) {
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(HTML_LAYOUT);

		if (moreFiltersLayout != null) {
			showHideMoreButton = new ToggleMoreFiltersComponent(moreFiltersLayout);
			layout.addComponent(showHideMoreButton, EXPAND_COLLAPSE_ID);
		}

		applyResetButtonsComponent = new ApplyResetButtonsComponent();
		layout.addComponent(applyResetButtonsComponent, APPLY_RESET_BUTTON_ID);

		addComponent(layout);
	}

	public void addResetHandler(Button.ClickListener resetHandler) {
		applyResetButtonsComponent.addResetHandler(resetHandler);
	}

	public void addApplyHandler(Button.ClickListener applyHandler) {
		applyResetButtonsComponent.addApplyHandler(applyHandler);
	}

	public void toggleMoreFilters(boolean shouldShowMoreFilters) {
		showHideMoreButton.toggleMoreFilters(shouldShowMoreFilters);
	}

	public void style(String style) {
		applyResetButtonsComponent.style(style);
	}
}
