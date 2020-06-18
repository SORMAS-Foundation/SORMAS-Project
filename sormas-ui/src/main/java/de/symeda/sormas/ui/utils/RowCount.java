package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;

public class RowCount extends HorizontalLayout {

	private Label labelValue;

	public RowCount(String labelCaption, int rowsCount) {
		createLayout(labelCaption, rowsCount);
	}

	private void createLayout(String caption, int rowsCount) {

		setMargin(false);
		addStyleName(CssStyles.VSPACE_4);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

		Label labelCaption = new Label(I18nProperties.getString(caption) + ":");
		labelCaption.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_NONE, CssStyles.ALIGN_RIGHT);
		labelCaption.setSizeFull();
		addComponent(labelCaption);
		setExpandRatio(labelCaption, 1);

		labelValue = new Label(String.valueOf(rowsCount));
		labelValue.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_NONE, CssStyles.ALIGN_RIGHT);
		labelValue.setSizeUndefined();
		addComponent(labelValue);
	}

	public void update(int rowsCount) {
		labelValue.setValue(String.valueOf(rowsCount));
	}

}
