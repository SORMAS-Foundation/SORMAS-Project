package de.symeda.sormas.ui.docgeneration;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocGenerationComponent extends VerticalLayout {

	public static final String QUARANTINE_LOC = "quarantine";

	private final ReferenceDto referenceDto;
	private final Button createButton;

	public static void addComponentToLayout(CustomLayout targetLayout, ReferenceDto referenceDto, QuarantineType quarantineType) {
		UserProvider currentUser = UserProvider.getCurrent();
		if (QuarantineType.isQuarantineInEffect(quarantineType)
			&& currentUser != null
			&& currentUser.hasUserRight(UserRight.QUARANTINE_ORDER_CREATE)) {
			DocGenerationComponent docgenerationComponent = new DocGenerationComponent(referenceDto);
			docgenerationComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			targetLayout.addComponent(docgenerationComponent, QUARANTINE_LOC);
		}
	}

	public DocGenerationComponent(ReferenceDto referenceDto) {
		super();
		this.referenceDto = referenceDto;

		setSpacing(false);

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
			Window window = VaadinUiUtil.showPopupWindow(new QuarantineOrderLayout(this.referenceDto));
			window.setWidth(800, Unit.PIXELS);
			window.setCaption(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder_create));
		});

		lineQuarantineOrder.addComponent(createButton);
		lineQuarantineOrder.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}
}
