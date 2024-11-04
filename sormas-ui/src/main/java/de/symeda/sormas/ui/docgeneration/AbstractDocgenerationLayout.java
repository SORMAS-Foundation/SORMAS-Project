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

import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractDocgenerationLayout extends VerticalLayout {

	protected final Button createButton;
	protected final Button cancelButton;
	public ComboBox<DocumentTemplateDto> templateSelector;
	public final VerticalLayout additionalVariablesComponent;
	public final VerticalLayout additionalParametersComponent;
	public FileDownloader fileDownloader;
	public DocumentVariables documentVariables;
	public CheckBox checkBoxUploadGeneratedDoc;
	public HorizontalLayout buttonBar;
	private Disease defaultDisease;

	protected AbstractDocgenerationLayout(
		Disease defaultDisease,
		String captionTemplateSelector,
		Function<DocumentTemplateDto, String> fileNameFunction,
		boolean isMultiFilesMode,
		boolean uploadCheckboxFirst) {

		this.defaultDisease = defaultDisease;

		additionalVariablesComponent = new VerticalLayout();
		additionalVariablesComponent.setSpacing(false);
		additionalVariablesComponent.setMargin(new MarginInfo(false, false, true, false));

		additionalParametersComponent = new VerticalLayout();
		additionalParametersComponent.setSpacing(false);
		additionalParametersComponent.setMargin(new MarginInfo(false, false, true, false));

		hideTextfields();
		hideAdditionalParameters();

		if (uploadCheckboxFirst) {
			addDiseaseSelector(defaultDisease);
			addTemplateSelector(captionTemplateSelector, fileNameFunction);
			addCheckboxUploadButton(isMultiFilesMode);
		} else {
			addCheckboxUploadButton(isMultiFilesMode);
			addDiseaseSelector(defaultDisease);
			addTemplateSelector(captionTemplateSelector, fileNameFunction);
		}

		createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCreate));
		createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		createButton.setIcon(VaadinIcons.FILE_TEXT);
		createButton.setEnabled(false);

		cancelButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCancel));
		cancelButton.addClickListener((e) -> closeWindow());

		buttonBar = new HorizontalLayout();
		buttonBar.addComponents(cancelButton, createButton);

		addComponent(additionalParametersComponent);
		addComponent(additionalVariablesComponent);
		addComponent(buttonBar);
		setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);
	}

	private void addCheckboxUploadButton(boolean isMultiFilesMode) {
		if (UiUtil.permitted(UserRight.DOCUMENT_UPLOAD)) {
			checkBoxUploadGeneratedDoc = new CheckBox(
				I18nProperties.getPrefixCaption(
					ExportConfigurationDto.I18N_PREFIX,
					isMultiFilesMode
						? Captions.DocumentTemplate_uploadGeneratedDocumentsToEntities
						: Captions.DocumentTemplate_uploadGeneratedDocumentToEntity));
			checkBoxUploadGeneratedDoc.setValue(false);
			checkBoxUploadGeneratedDoc.setEnabled(true);
			checkBoxUploadGeneratedDoc.setStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);
			checkBoxUploadGeneratedDoc.setWidth(400, Unit.PIXELS);
			addComponent(checkBoxUploadGeneratedDoc);
		}
	}

	private void addDiseaseSelector(Disease defaultDisease) {
		ComboBox<Disease> diseaseSelector = new ComboBox<>();
		diseaseSelector.setCaption(I18nProperties.getCaption(Captions.disease));
		diseaseSelector.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseSelector.setPlaceholder(I18nProperties.getString(Strings.all));
		diseaseSelector.setEmptySelectionAllowed(true);
		diseaseSelector.setWidth(100F, Unit.PERCENTAGE);
		diseaseSelector.addStyleName(CssStyles.SOFT_REQUIRED);
		diseaseSelector.setValue(defaultDisease);

		diseaseSelector.addValueChangeListener(e -> {
			Disease disease = e.getValue();
			templateSelector.setValue(null);

			templateSelector.setItems(getAvailableTemplates(disease));
			templateSelector.setItemCaptionGenerator(DocumentTemplateDto::getFileName);
		});

		addComponent(diseaseSelector);
	}

	private void addTemplateSelector(String captionTemplateSelector, Function<DocumentTemplateDto, String> fileNameFunction) {
		templateSelector = new ComboBox<>(captionTemplateSelector);
		templateSelector.setWidth(100F, Unit.PERCENTAGE);
		templateSelector.addValueChangeListener(e -> {
			DocumentTemplateDto template = e.getValue();
			boolean isValidTemplateFile = template != null;
			createButton.setEnabled(isValidTemplateFile);
			additionalVariablesComponent.removeAllComponents();
			hideTextfields();
			documentVariables = null;
			if (isValidTemplateFile) {
				try {
					documentVariables = getDocumentVariables(template.toReference());
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
					if (fileNameFunction != null) {
						setStreamResource(template, fileNameFunction.apply(template));
					}
				} catch (IOException | DocumentTemplateException ex) {
					LoggerFactory.getLogger(getClass()).error("Error while reading document variables.", e);
					new Notification(I18nProperties.getString(Strings.errorOccurred), ex.getMessage(), Notification.Type.ERROR_MESSAGE, false)
						.show(Page.getCurrent());
				}
			}
		});
		templateSelector.addStyleName(CssStyles.SOFT_REQUIRED);

		addComponent(templateSelector);
	}

	protected void init() {
		templateSelector.setItems(getAvailableTemplates(defaultDisease));
		templateSelector.setItemCaptionGenerator(DocumentTemplateDto::getFileName);
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

	private void setStreamResource(DocumentTemplateDto template, String fileName) {
		StreamResource streamResource = createStreamResource(template, fileName);
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

	protected boolean shouldUploadGeneratedDocument() {
		return checkBoxUploadGeneratedDoc != null && Boolean.TRUE.equals(checkBoxUploadGeneratedDoc.getValue());
	}

	protected abstract List<DocumentTemplateDto> getAvailableTemplates(Disease disease);

	protected abstract DocumentVariables getDocumentVariables(DocumentTemplateReferenceDto templateReference)
		throws IOException, DocumentTemplateException;

	protected abstract StreamResource createStreamResource(DocumentTemplateDto template, String filename);

	protected abstract String getWindowCaption();
}
