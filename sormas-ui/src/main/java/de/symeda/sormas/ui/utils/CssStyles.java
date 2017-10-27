package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public final class CssStyles {

	public static final String H1 = "h1";
	public static final String H2 = "h2";
	public static final String H3 = "h3";
	public static final String H4 = "h4";
	public static final String H5 = "h5";

	public static final String VSPACE_0 = "vspace-0";
	public static final String VSPACE_1 = "vspace-1";
	public static final String VSPACE_2 = "vspace-2";
	public static final String VSPACE_3 = "vspace-3";
	public static final String VSPACE_4 = "vspace-4";
	public static final String VSPACE_5 = "vspace-5";
	public static final String VSPACE_NONE = "vspace-none";
	
	public static final String VSPACE_TOP_0 = "vspace-top-0";
	public static final String VSPACE_TOP_1 = "vspace-top-1";
	public static final String VSPACE_TOP_2 = "vspace-top-2";
	public static final String VSPACE_TOP_3 = "vspace-top-3";
	public static final String VSPACE_TOP_4 = "vspace-top-4";
	public static final String VSPACE_TOP_5 = "vspace-top-5";
	public static final String VSPACE_TOP_NONE = "vspace-top-none";
	
	public static final String HSPACE_RIGHT_0 = "hspace-right-0";
	public static final String HSPACE_RIGHT_1 = "hspace-right-1";
	public static final String HSPACE_RIGHT_2 = "hspace-right-2";
	public static final String HSPACE_RIGHT_3 = "hspace-right-3";
	public static final String HSPACE_RIGHT_4 = "hspace-right-4";
	public static final String HSPACE_RIGHT_5 = "hspace-right-5";
	public static final String HSPACE_RIGHT_NONE = "hspace-top-none";

	public static final String BUTTON_SUBTLE = "subtle";
	
	public static final String LINK_ACTIVE = "active";

	/**
	 * Stellt sicher, dass CM-Checkboxen dieselbe Höhe wie Textfelder etc. haben
	 */
	public static final String FORCE_CAPTION = "force-caption";
	public static final String CAPTION_HIDDEN = "caption-hidden";

	public static final String ALIGN_RIGHT = "align-right";

	public static final String OPTIONGROUP_CAPTION_INLINE = "caption-inline";

	public static final String GRID_CELL_PRIORITY_HIGH = "priority-high";
	public static final String GRID_CELL_PRIORITY_NORMAL = "priority-normal";
	public static final String GRID_CELL_PRIORITY_LOW = "priority-low";

	public static final String GRID_CELL_WARNING = "warning";

	public static final String GRID_ROW_STATUS_DISCARDED = "status-discarded";
	public static final String GRID_ROW_STATUS_NOT = "status-not";
	public static final String GRID_ROW_STATUS_DONE = "status-done";
	public static final String GRID_ROW_STATUS_PENDING = "status-pending";
	
	@Deprecated
	public static final String CALLOUT = "callout";

	public static void style(Component component, String... styles) {
		for (String style : styles)
			component.addStyleName(style);
	}

	public static void stylePrimary(Component component, String primaryStyle, String... styles) {
		component.setPrimaryStyleName(primaryStyle);
		for (String style : styles)
			component.addStyleName(style);
	}

	/**
	 * Styles and (de-)activates the given buttons.
	 * 
	 * @param activeButton
	 *		This button is styled as active but disabled.
	 * @param allOrOtherButtons
	 *		These buttons lose their active styling and become enabled. {@code activeButton} may be included here.
	 */
	public static void styleSectionFilterButton(Button activeButton, Button... allOrOtherButtons) {

		styleSectionFilterButton(activeButton, allOrOtherButtons == null ? Collections.emptyList() : Arrays.asList(allOrOtherButtons));
	}

	/**
	 * Styles and (de-)activates the given buttons.
	 * 
	 * @param activeButton
	 *		This button is styled as active but disabled.
	 * @param allOrOtherButtons
	 *		These buttons lose their active styling and become enabled. {@code activeButton} may be included here.
	 */
	public static <B extends Button> void styleSectionFilterButton(Button activeButton, Iterable<B> allOrOtherButtons) {

		for (Button button : allOrOtherButtons) {
			button.setEnabled(true);
			button.removeStyleName(CssStyles.LINK_ACTIVE);
		}

		activeButton.setEnabled(false);
		activeButton.addStyleName(CssStyles.LINK_ACTIVE);
	}
}
