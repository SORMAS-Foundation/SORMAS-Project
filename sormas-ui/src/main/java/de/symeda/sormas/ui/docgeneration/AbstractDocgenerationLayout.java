/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractDocgenerationLayout extends VerticalLayout {

	protected final Button createButton;
	protected final Button cancelButton;
	protected ComboBox<String> templateSelector;
	protected final VerticalLayout additionalVariablesComponent;
	protected final VerticalLayout additionalParametersComponent;
	protected FileDownloader fileDownloader;
	protected DocumentVariables documentVariables;

	public AbstractDocgenerationLayout(String captionTemplateSelector, Function<String, String> fileNameFunction) {
		additionalVariablesComponent = new VerticalLayout();
		additionalVariablesComponent.setSpacing(false);
		additionalVariablesComponent.setMargin(new MarginInfo(false, false, true, false));

		additionalParametersComponent = new VerticalLayout();
		additionalParametersComponent.setSpacing(false);
		additionalParametersComponent.setMargin(new MarginInfo(false, false, true, false));

		hideTextfields();
		hideAdditionalParameters();

		createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.setEnabled(false);

		cancelButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCancel));
		cancelButton.addClickListener((e) -> closeWindow());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.addComponents(cancelButton, createButton);

		templateSelector = new ComboBox<>(captionTemplateSelector);
		templateSelector.setWidth(100F, Unit.PERCENTAGE);
		templateSelector.addValueChangeListener(e -> {
			String templateFile = e.getValue();
			boolean isValidTemplateFile = StringUtils.isNotBlank(templateFile);
			createButton.setEnabled(isValidTemplateFile);
			additionalVariablesComponent.removeAllComponents();
			hideTextfields();
			documentVariables = null;
			if (isValidTemplateFile) {
				try {
					documentVariables = getDocumentVariables(templateFile);
					List<String> additionalVariables = documentVariables.getAdditionalVariables();
					if (additionalVariables != null && !additionalVariables.isEmpty()) {
						for (String variable : additionalVariables) {
							TextField variableInput = new TextField(variable);
							variableInput.setWidth(100F, Unit.PERCENTAGE);
							additionalVariablesComponent.addComponent(variableInput);
						}
						showTextfields();
					}
					performTemplateUpdates();
					setStreamResource(templateFile, fileNameFunction.apply(templateFile));
				} catch (Exception ex) {
					ex.printStackTrace();
					new Notification(I18nProperties.getString(Strings.errorOccurred), ex.getMessage(), Notification.Type.ERROR_MESSAGE, false)
						.show(Page.getCurrent());
				}
			}
		});
		templateSelector.addStyleName(CssStyles.SOFT_REQUIRED);

		addComponent(templateSelector);
		addComponent(additionalParametersComponent);
		addComponent(additionalVariablesComponent);
		addComponent(buttonBar);
		setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);
	}

	protected void init() {
		templateSelector.setItems(getAvailableTemplates());
	}

	private void showTextfields() {
		additionalVariablesComponent.setVisible(true);
		adjustSpacing();
	}

	private void hideTextfields() {
		additionalVariablesComponent.setVisible(false);
		adjustSpacing();
	}

	protected void showAdditionalParameters() {
		additionalParametersComponent.setVisible(true);
		adjustSpacing();
	}

	protected void hideAdditionalParameters() {
		additionalParametersComponent.setVisible(false);
		adjustSpacing();
	}

	private void adjustSpacing() {
		setSpacing(!(additionalVariablesComponent.isVisible() || additionalParametersComponent.isVisible()));
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

	private void setStreamResource(String templateFile, String fileName) {
		StreamResource streamResource = createStreamResource(templateFile, fileName);
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(createButton);
		} else {
			fileDownloader.setFileDownloadResource(streamResource);
		}
	}

	protected void performTemplateUpdates() {
		// do nothing
	}

	protected abstract List<String> getAvailableTemplates();

	protected abstract DocumentVariables getDocumentVariables(String templateFile) throws IOException, DocumentTemplateException;

	protected abstract StreamResource createStreamResource(String templateFile, String filename);

	protected abstract String getWindowCaption();
}
