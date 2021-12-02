package de.symeda.sormas.ui.utils.components.page.title;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

public class RowLayout extends HorizontalLayout {

	public void addToLayout(String text, String... styles) {
		if (StringUtils.isNotBlank(text)) {
			Label label = new Label(text);
			label.addStyleNames(styles);
			label.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
			addComponent(label);
		}
	}
}
