package de.symeda.sormas.ui.utils.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.HorizontalLayout;

public class FormActionButtonsComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1159701271006170763L;

	private ToggleMoreFiltersComponent showHideMoreButton;
	private final ApplyResetButtonsComponent applyResetButtonsComponent;

	public FormActionButtonsComponent(String applyCaptionTag, String resetCaptionTag, CustomLayout moreFiltersLayout) {
		if (moreFiltersLayout != null) {
			showHideMoreButton = new ToggleMoreFiltersComponent(moreFiltersLayout);
			addComponent(showHideMoreButton);
		}

		applyResetButtonsComponent = new ApplyResetButtonsComponent(applyCaptionTag, resetCaptionTag);
		addComponent(applyResetButtonsComponent);
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
