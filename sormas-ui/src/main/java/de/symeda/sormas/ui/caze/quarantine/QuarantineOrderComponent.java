package de.symeda.sormas.ui.caze.quarantine;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class QuarantineOrderComponent extends VerticalLayout {

	private final CaseReferenceDto caseReferenceDto;

	private final Button createButton;
	private VerticalLayout additionalVariablesComponent;
	private FileDownloader fileDownloader;

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
			additionalVariablesComponent.removeAllComponents();
			if (isValidTemplateFile) {
				List<String> additionalVariables = FacadeProvider.getQuarantineOrderFacade().getAdditionalVariables(templateFile);
				for (String variable : additionalVariables) {
					TextField variableInput = new TextField(variable);
					variableInput.setWidth(80F, Unit.PERCENTAGE);
					additionalVariablesComponent.addComponent(variableInput);
				}
				setStreamResource(templateFile);
			}
		});
		addComponent(templateSelector);

		additionalVariablesComponent = new VerticalLayout();
		additionalVariablesComponent.setSpacing(false);
		addComponent(additionalVariablesComponent);

		componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
	}

	private Properties readAdditionalVariables() {
		Properties properties = new Properties();
		for (int i = 0; i < additionalVariablesComponent.getComponentCount(); i++) {
			Component component = additionalVariablesComponent.getComponent(i);
			if (component instanceof TextField) {
				TextField textField = (TextField) component;
				properties.setProperty(textField.getCaption(), textField.getValue());
			}
		}
		return properties;
	}

	private void setStreamResource(String templateFile) {
		StreamResource streamResource = new StreamResource((StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			return new ByteArrayInputStream(
				quarantineOrderFacade.getGeneratedDocument(templateFile, caseReferenceDto.getUuid(), readAdditionalVariables()));
		}, caseReferenceDto.getUuid() + templateFile);
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(createButton);
		} else {
			fileDownloader.setFileDownloadResource(streamResource);
		}
	}
}
