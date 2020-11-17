package de.symeda.sormas.ui.docgeneration;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventDocumentsComponent extends VerticalLayout {

	public static final String DOCGENERATION_LOC = "docgeneration";

	private final Button createButton;

	public EventDocumentsComponent(EventReferenceDto eventReferenceDto) {
		super();

		setSpacing(false);

		// TODO: I18N
		Label headerDocgeneration = new Label("Dokumente");
		headerDocgeneration.addStyleName(CssStyles.H3);
		addComponent(headerDocgeneration);

		HorizontalLayout lineEventDocuments = new HorizontalLayout();
		lineEventDocuments.setMargin(false);
		lineEventDocuments.setSpacing(false);
		lineEventDocuments.setWidth(100, Unit.PERCENTAGE);
		addComponent(lineEventDocuments);

		// TODO: I18N
		Label labelEventDocuments = new Label("Ereignis-Formular");
		lineEventDocuments.addComponent(labelEventDocuments);

		createButton = new Button(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.addClickListener((Button.ClickListener) clickEvent -> {
			Window window = VaadinUiUtil.showPopupWindow(new EventDocumentLayout(eventReferenceDto));
			window.setWidth(800, Unit.PIXELS);
			// TODO: I18N
			window.setCaption("Ereignis-Formular erstellen");
		});

		lineEventDocuments.addComponent(createButton);
		lineEventDocuments.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}
}
