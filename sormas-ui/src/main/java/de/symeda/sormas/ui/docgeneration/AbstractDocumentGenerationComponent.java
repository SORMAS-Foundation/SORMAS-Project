package de.symeda.sormas.ui.docgeneration;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractDocumentGenerationComponent extends VerticalLayout {

	public AbstractDocumentGenerationComponent() {
		super();

		setSpacing(false);

		Label headerDocgeneration = new Label(I18nProperties.getCaption(getComponentLabel()));
		headerDocgeneration.addStyleName(CssStyles.H3);
		addComponent(headerDocgeneration);
	}

	protected void addDocumentBar(Runnable createHandler, String documentLabel) {
		HorizontalLayout documentBar = new HorizontalLayout();
		documentBar.setMargin(false);
		documentBar.setSpacing(false);
		documentBar.setWidth(100, Unit.PERCENTAGE);
		addComponent(documentBar);

		Label labelDocument = new Label(I18nProperties.getCaption(documentLabel));
		documentBar.addComponent(labelDocument);

		Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.addClickListener((Button.ClickListener) clickEvent -> {
			createHandler.run();
		});

		documentBar.addComponent(createButton);
		documentBar.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}

	protected String getComponentLabel() {
		return I18nProperties.getCaption(Captions.DocumentTemplate_plural);
	}
}
