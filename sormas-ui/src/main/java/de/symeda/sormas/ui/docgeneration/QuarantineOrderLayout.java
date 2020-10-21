package de.symeda.sormas.ui.docgeneration;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ValidationException;

public class QuarantineOrderLayout extends VerticalLayout {

	private final ReferenceDto caseReferenceDto;

	private final Button createButton;
	private final VerticalLayout additionalVariablesComponent;
	private FileDownloader fileDownloader;

	public QuarantineOrderLayout(ReferenceDto referenceDto) {
		super();
		this.caseReferenceDto = referenceDto;

		additionalVariablesComponent = new VerticalLayout();
		additionalVariablesComponent.setSpacing(false);
		additionalVariablesComponent.setVisible(false);

		createButton = new Button(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.setEnabled(false);

		ComboBox<String> templateSelector = new ComboBox<>(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder));
		templateSelector.setWidth(80F, Unit.PERCENTAGE);
		templateSelector.setItems(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates());
		templateSelector.addValueChangeListener(e -> {
			String templateFile = e.getValue();
			boolean isValidTemplateFile = StringUtils.isNotBlank(templateFile);
			createButton.setEnabled(isValidTemplateFile);
			additionalVariablesComponent.removeAllComponents();
			additionalVariablesComponent.setVisible(false);
			if (isValidTemplateFile) {
				try {
					List<String> additionalVariables = FacadeProvider.getQuarantineOrderFacade().getAdditionalVariables(templateFile);
					if (additionalVariables != null && !additionalVariables.isEmpty()) {
						for (String variable : additionalVariables) {
							TextField variableInput = new TextField(variable);
							variableInput.setWidth(80F, Unit.PERCENTAGE);
							additionalVariablesComponent.addComponent(variableInput);
						}
						additionalVariablesComponent.setVisible(true);
					}
					setStreamResource(templateFile);
				} catch (ValidationException validationException) {
					validationException.printStackTrace();
					new Notification("Error I18N", "Error Description I18N", Notification.Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		});

		addComponent(templateSelector);
		addComponent(additionalVariablesComponent);
		addComponent(createButton);
	}

	private Properties readAdditionalVariables() {
		Properties properties = new Properties();
		forAllVariableInputs(textField -> {
			properties.setProperty(textField.getCaption(), textField.getValue());
			return null;
		});
		return properties;
	}

	private void forAllVariableInputs(Function<TextField, Void> function) {
		for (int i = 0; i < additionalVariablesComponent.getComponentCount(); i++) {
			Component component = additionalVariablesComponent.getComponent(i);
			if (component instanceof TextField) {
				TextField textField = (TextField) component;
				function.apply(textField);
			}
		}
	}

	private void setStreamResource(String templateFile) {
		String uuid = caseReferenceDto.getUuid();
		String filename = uuid.substring(0, Math.min(5, uuid.length())) + "_" + templateFile;
		StreamResource streamResource = new StreamResource((StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			try {
				ByteArrayInputStream byteArrayInputStream =
					new ByteArrayInputStream(quarantineOrderFacade.getGeneratedDocument(templateFile, caseReferenceDto, readAdditionalVariables()));
				closeWindow();
				return byteArrayInputStream;
			} catch (ValidationException e) {
				new Notification("Document generation failed", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				return null;
			}
		}, filename);
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(createButton);
		} else {
			fileDownloader.setFileDownloadResource(streamResource);
		}
	}

	private void closeWindow() {
		HasComponents parent = this.getParent();
		if (parent instanceof Window && ((Window) parent).isClosable()) {
			((Window) parent).close();
		}
	}
}
