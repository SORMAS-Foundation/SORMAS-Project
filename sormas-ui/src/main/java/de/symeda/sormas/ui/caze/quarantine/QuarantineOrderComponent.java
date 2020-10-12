package de.symeda.sormas.ui.caze.quarantine;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class QuarantineOrderComponent extends VerticalLayout {

	private final ReferenceDto referenceDto;
	private final Button createButton;

	public QuarantineOrderComponent(ReferenceDto caseReferenceDto) {
		super();
		this.referenceDto = caseReferenceDto;

		Label headerDocgeneration = new Label(I18nProperties.getCaption(Captions.caseDocuments));
		headerDocgeneration.addStyleName(CssStyles.H3);
		addComponent(headerDocgeneration);

		HorizontalLayout lineQuarantineOrder = new HorizontalLayout();
		lineQuarantineOrder.setMargin(false);
		lineQuarantineOrder.setSpacing(false);
		lineQuarantineOrder.setWidth(100, Unit.PERCENTAGE);
		addComponent(lineQuarantineOrder);

		Label labelQuarantineOrder = new Label(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder));
		lineQuarantineOrder.addComponent(labelQuarantineOrder);

		createButton = new Button(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.addClickListener((Button.ClickListener) clickEvent -> {
			Window window = VaadinUiUtil.showPopupWindow(new CreateQuarantineOrderLayout(referenceDto));
			window.setWidth(800, Unit.PIXELS);
			window.setCaption(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder_create));
		});

		lineQuarantineOrder.addComponent(createButton);
		lineQuarantineOrder.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}
}
