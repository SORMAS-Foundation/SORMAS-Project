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
	public static final String NO_MARGIN = "no-margin";
	
	public static final String SUBLIST_PADDING = "sublist-padding";

	public static final String VSPACE0 = "vspace0";
	public static final String VSPACE1 = "vspace1";
	public static final String VSPACE2 = "vspace2";
	public static final String VSPACE3 = "vspace3";
	public static final String VSPACE4 = "vspace4";
	public static final String VSPACE5 = "vspace5";
	public static final String VSPACE_NO_FILTERS = "vspace-no-filters";
	public static final String VSPACETOP3 = "vspacetop3";
	public static final String VSPACETOP4 = "vspacetop4";

	public static final String SELECTABLE = "selectable";

	public static final String PULL_LEFT = "pull-left";
	public static final String PULL_RIGHT = "pull-right";

	public static final String BUTTON = "btn";
	public static final String BUTTON_LINK = "btn-link";
	public static final String BUTTON_LINK_FIRST = "btn-link-first";
	public static final String BUTTON_LINK_LAST = "btn-link-last";
	public static final String BUTTON_PRIMARY = "btn-primary";
	public static final String BUTTON_LARGE = "btn-large";
	public static final String BUTTON_SMALL = "btn-small";
	public static final String BUTTON_MINI = "btn-mini";
	/**
	 * Receives same margins as a field
	 */
	public static final String BUTTON_FIELD = "btn-field";
	/**
	 * Puts the options right aligned in line with the caption
	 */
	public static final String ROW_OPTIONGROUP = "row-optiongroup";
	/**
	 * Puts the options in one line
	 */
	public static final String INLINE_OPTIONGROUP = "inline-optiongroup";
		
	public static final String LINK_ACTIVE = "active";

	public static final String LABEL_SMALL = "small";
	public static final String LABEL_MEDIUM = "medium";
	public static final String LABEL_PROMPT = "prompt";

	public static final String BADGE = "badge";
	public static final String BADGE_INFO = "badge-info";

	public static final String BUTTON_UPLOAD = "button-upload";

	public static final String FLOW_LAYOUT = "flow-layout";

	/**
	 * Stellt sicher, dass CM-Checkboxen dieselbe Höhe wie Textfelder etc. haben
	 */
	public static final String FORCE_CAPTION = "force-caption";
	public static final String CAPTION_HIDDEN = "caption-hidden";

	public static final String FONT_CROSSED = "font-crossed";

	public static final String ALIGN_RIGHT = "align-right";

	public static final String ALIGN_CENTERED = "align-centered";
	
	public static final String PRIORITY_HIGH = "priority-high";
	public static final String PRIORITY_NORMAL = "priority-normal";
	public static final String PRIORITY_LOW = "priority-low";

	public static final String WARNING = "warning";

	public static final String STATUS_DISCARDED = "status-discarded";
	public static final String STATUS_NOT = "status-not";
	public static final String STATUS_DONE = "status-done";
	public static final String STATUS_PENDING = "status-pending";
	
	public static final String CALLOUT = "callout";
	public static final String DASHBOARD_KEY = "dashboard-key";
	
	public static final String CURSOR_LINK = "cursor-link";
	
	public static final String FONT_SIZE_LARGE = "font-size-large";
	public static final String FONT_SIZE_XLARGE = "font-size-xlarge";
	public static final String FONT_SIZE_SMALL = "font-size-small";
	
	public static final String SUBLIST_MARGIN = "sublist-margin";
	public static final String SUBLIST_MARGIN_SMALL = "sublist-margin-small";
	public static final String INFO_COLUMN_MARGIN = "info-column-margin";
	
	public static final String COLOR_RED = "color-red";
	public static final String COLOR_GREEN = "color-green";
	public static final String COLOR_GREY = "color-grey";


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
