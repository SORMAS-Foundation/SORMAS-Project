package de.symeda.sormas.ui.caze.quarantine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

	private Button createButton;

	public QuarantineOrderComponent(CaseReferenceDto caseReferenceDto) {
		super();
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
		createButton.setIcon(VaadinIcons.DOWNLOAD);
		// createButton.addClickListener(clickListener);
		componentHeader.addComponent(createButton);

		StreamResource streamResource = new StreamResource(new StreamSource() {

			@Override
			public InputStream getStream() {
				QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
				return new ByteArrayInputStream(
					quarantineOrderFacade.getGeneratedDocument("Quarantine.docx", caseReferenceDto.getUuid(), new Properties()));
			}
		}, "Quarantine.docx");
		new FileDownloader(streamResource).extend(createButton);

		componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}
}
