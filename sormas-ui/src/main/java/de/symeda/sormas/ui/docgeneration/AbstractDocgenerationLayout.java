package de.symeda.sormas.ui.docgeneration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractDocgenerationLayout extends VerticalLayout {

	protected final Button createButton;
	protected final Button cancelButton;
	protected final VerticalLayout additionalVariablesComponent;
	protected FileDownloader fileDownloader;

	public AbstractDocgenerationLayout(String captionTemplateSelector) {
		additionalVariablesComponent = new VerticalLayout();
		additionalVariablesComponent.setSpacing(false);
		additionalVariablesComponent.setMargin(new MarginInfo(false, false, true, false));
		hideTextfields();

		createButton = new Button(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.setEnabled(false);

		cancelButton = new Button(I18nProperties.getCaption(Captions.actionCancel));
		cancelButton.addClickListener((e) -> closeWindow());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.addComponents(cancelButton, createButton);

		ComboBox<String> templateSelector = new ComboBox<>(captionTemplateSelector);
		templateSelector.setWidth(100F, Unit.PERCENTAGE);
		templateSelector.setItems(getAvailableTemplates());
		templateSelector.addValueChangeListener(e -> {
			String templateFile = e.getValue();
			boolean isValidTemplateFile = StringUtils.isNotBlank(templateFile);
			createButton.setEnabled(isValidTemplateFile);
			additionalVariablesComponent.removeAllComponents();
			hideTextfields();
			if (isValidTemplateFile) {
				try {
					List<String> additionalVariables = getAdditionalVariables(templateFile);
					if (additionalVariables != null && !additionalVariables.isEmpty()) {
						for (String variable : additionalVariables) {
							TextField variableInput = new TextField(variable);
							variableInput.setWidth(100F, Unit.PERCENTAGE);
							additionalVariablesComponent.addComponent(variableInput);
						}
						showTextfields();
					}
					setStreamResource(templateFile);
				} catch (IOException ioException) {
					ioException.printStackTrace();
					new Notification(
						I18nProperties.getString(Strings.errorOccurred),
						ioException.getMessage(),
						Notification.Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		});
		templateSelector.addStyleName(CssStyles.SOFT_REQUIRED);

		addComponent(templateSelector);
		addComponent(additionalVariablesComponent);
		addComponent(buttonBar);
		setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);
	}

	private void showTextfields() {
		additionalVariablesComponent.setVisible(true);
		setSpacing(false);
	}

	private void hideTextfields() {
		additionalVariablesComponent.setVisible(false);
		setSpacing(true);
	}

	private void closeWindow() {
		HasComponents parent = this.getParent();
		if (parent instanceof Window && ((Window) parent).isClosable()) {
			((Window) parent).close();
		}
	}

	protected Properties readAdditionalVariables() {
		Properties properties = new Properties();
		doForAllVariableInputs(textField -> {
			properties.setProperty(textField.getCaption(), textField.getValue());
			return null;
		});
		return properties;
	}

	private void doForAllVariableInputs(Function<TextField, Void> function) {
		for (int i = 0; i < additionalVariablesComponent.getComponentCount(); i++) {
			Component component = additionalVariablesComponent.getComponent(i);
			if (component instanceof TextField) {
				TextField textField = (TextField) component;
				function.apply(textField);
			}
		}
	}

	private void setStreamResource(String templateFile) {
		String filename = generateFilename(templateFile);
		StreamResource streamResource = createStreamResource(templateFile, filename);
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(createButton);
		} else {
			fileDownloader.setFileDownloadResource(streamResource);
		}
	}

	protected abstract List<String> getAvailableTemplates();

	protected abstract String generateFilename(String templateFile);

	protected abstract List<String> getAdditionalVariables(String templateFile) throws IOException;

	protected abstract StreamResource createStreamResource(String templateFile, String filename);

	protected abstract String getWindowCaption();
}
