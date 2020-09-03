package de.symeda.sormas.ui;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class SearchSpecificLayout extends VerticalLayout {

	public SearchSpecificLayout(
		Runnable confirmCallback,
		Runnable closePopupCallback,
		TextField searchField,
		String searchDescription,
		String confirmCaption) {

		setMargin(true);
		setSpacing(false);
		setWidth(100, Unit.PERCENTAGE);

		Label lblSearchDescription = new Label(searchDescription, ContentMode.HTML);
		lblSearchDescription.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(lblSearchDescription, CssStyles.VSPACE_2);
		addComponent(lblSearchDescription);

		ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

			@Override
			protected void onConfirm() {
				confirmCallback.run();
			}

			@Override
			protected void onCancel() {
				closePopupCallback.run();
			}
		};

		searchField.setPlaceholder(I18nProperties.getString(Strings.promptSearch));
		searchField.setWidth(300, Unit.PIXELS);
		searchField.addValueChangeListener(e -> confirmationComponent.getConfirmButton().setEnabled(StringUtils.isNotEmpty(e.getValue())));
		CssStyles.style(searchField, CssStyles.VSPACE_2);
		addComponent(searchField);

		confirmationComponent.getConfirmButton().setCaption(confirmCaption);
		confirmationComponent.getConfirmButton().setEnabled(false);
		confirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		confirmationComponent.setMargin(true);
		addComponent(confirmationComponent);
		setComponentAlignment(confirmationComponent, Alignment.MIDDLE_RIGHT);
	}
}
