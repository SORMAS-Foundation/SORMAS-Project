package de.symeda.sormas.ui.caze.quarantine;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class QuarantineOrderComponent extends VerticalLayout {

	private final Button createButton;
	private final CaseReferenceDto caseReferenceDto;

	public QuarantineOrderComponent(CaseReferenceDto caseReferenceDto) {
		super();
		this.caseReferenceDto = caseReferenceDto;
		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label tasksHeader = new Label(I18nProperties.getString(Strings.entityQuarantineOrder));
		tasksHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(tasksHeader);

		createButton = new Button(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.setEnabled(false);
		// createButton.addClickListener(clickListener);
		componentHeader.addComponent(createButton);

		ComboBox<String> templateSelector = new ComboBox<>();
		templateSelector.setItems(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates());
		templateSelector.addValueChangeListener(e -> {
			String templateFile = e.getValue();
			boolean isValidTemplateFile = templateFile != null && !templateFile.isEmpty();
			createButton.setEnabled(isValidTemplateFile);
			if (isValidTemplateFile) {
				setStreamResource(templateFile);
			}
		});
		addComponent(templateSelector);

		componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}

	private void setStreamResource(String templateFile) {
		StreamResource streamResource = new StreamResource((StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			return new ByteArrayInputStream(quarantineOrderFacade.getGeneratedDocument(templateFile, caseReferenceDto.getUuid(), new Properties()));
		}, caseReferenceDto.getUuid() + templateFile);
		new FileDownloader(streamResource).extend(createButton);
	}
}
