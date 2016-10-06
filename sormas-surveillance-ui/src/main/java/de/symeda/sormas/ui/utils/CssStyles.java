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

	public static final String VSPACE0 = "vspace0";
	public static final String VSPACE1 = "vspace1";
	public static final String VSPACE2 = "vspace2";
	public static final String VSPACE3 = "vspace3";
	public static final String VSPACE4 = "vspace4";
	public static final String VSPACE5 = "vspace5";

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

	public static final String FONT_CROSSED = "font-crossed";

	public static final String ALIGN_RIGHT = "align-right";

	public static final String ALIGN_CENTERED = "align-centered";


	public static void style(Component component, String... styles) {
		for (String style : styles)
			component.addStyleName(style);
	}

	public static void stylePrimary(Component component, String primaryStyle, String... styles) {
		component.setPrimaryStyleName(primaryStyle);
		for (String style : styles)
			component.addStyleName(style);
	}

//	public static void style(CommitDiscardWrapperComponent<?> component) {
//		component.getCommitButton().setPrimaryStyleName(CssStyles.BUTTON);
//		component.getCommitButton().addStyleName(CssStyles.BUTTON_PRIMARY);
//
//		component.getDiscardButton().setPrimaryStyleName(CssStyles.BUTTON);
//		component.getDiscardButton().addStyleName(CssStyles.BUTTON_LINK);
//
//		ConfirmationComponent deleteConfirmationComponent = component.getDeleteConfirmationComponent();
//		NativeButton cancelButton = deleteConfirmationComponent.getCancelButton();
//		stylePrimary(cancelButton, CssStyles.BUTTON, CssStyles.BUTTON_LINK);
//
//		NativeButton commitButton = deleteConfirmationComponent.getConfirmButton();
//		stylePrimary(commitButton, CssStyles.BUTTON, CssStyles.BUTTON_PRIMARY);
//
//		NativeButton deleteButton = component.getDeleteButton();
//		stylePrimary(deleteButton, CssStyles.BUTTON, CssStyles.BUTTON_LINK);
//	}

	/**
	 * Stylt und (de-)aktiviert die übergebenen Buttons.
	 * 
	 * @param activeButton
	 *            Dieser Button wird als aktiv gestylt, aber disabled.
	 * @param allOrOtherButtons
	 *            Diese Buttons verlieren das Aktiv-Styling und werden enabled. {@code activeButton} darf hier enthalten sein.
	 */
	public static void styleSectionFilterButton(Button activeButton, Button... allOrOtherButtons) {

		styleSectionFilterButton(activeButton, allOrOtherButtons == null ? Collections.emptyList() : Arrays.asList(allOrOtherButtons));
	}

	/**
	 * Stylt und (de-)aktiviert die übergebenen Buttons.
	 * 
	 * @param activeButton
	 *            Dieser Button wird als aktiv gestylt, aber disabled.
	 * @param allOrOtherButtons
	 *            Diese Buttons verlieren das Aktiv-Styling und werden enabled. {@code activeButton} darf hier enthalten sein.
	 */
	public static <B extends Button> void styleSectionFilterButton(Button activeButton, Iterable<B> allOrOtherButtons) {

		for (Button button : allOrOtherButtons) {
			button.setEnabled(true);
			button.removeStyleName(CssStyles.LINK_ACTIVE);
		}

		activeButton.setEnabled(false);
		activeButton.addStyleName(CssStyles.LINK_ACTIVE);
	}

//	/**
//	 * Konkateniert die css-Klassen
//	 *
//	 * @param classes
//	 * @return
//	 */
//	public static String all(String... classes) {
//		return org.apache.commons.lang3.StringUtils.join(classes, " ");
//	}
}
