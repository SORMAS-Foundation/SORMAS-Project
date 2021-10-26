package de.symeda.sormas.ui.utils.components.page.title;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class TitleLayout extends VerticalLayout {

	public TitleLayout() {
		addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		setSpacing(false);
	}

	public void addRow(String text) {
		addRow(text, CssStyles.H3);
	}

	public void addRow(RowLayout rowLayout) {
		addComponent(rowLayout);
	}

	public void addMainRow(String text) {
		addRow(text, CssStyles.H2, CssStyles.LABEL_PRIMARY);
	}

	private void addRow(String text, String... styles) {
		if (StringUtils.isNotBlank(text)) {
			Label row = new Label(text);
			row.addStyleNames(styles);
			row.addStyleNames(CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_CAPTION_TRUNCATED);
			addComponent(row);
		}
	}
}
