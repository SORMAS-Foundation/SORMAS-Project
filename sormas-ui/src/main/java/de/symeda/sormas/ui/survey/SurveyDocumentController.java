/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.survey;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.externalemail.AttachmentException;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.survey.SurveyDocumentOptionsDto;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveyDocumentController {

	public void sendSurveyDocument(
		RootEntityType rootEntityType,
		ReferenceDto rootEntityReference,
		Disease disease,
		PersonDto person,
		Runnable callback) {
		SurveyDocumentOptionsForm form = new SurveyDocumentOptionsForm(disease, true, person);
		form.setWidth(600, Sizeable.Unit.PIXELS);
		SurveyDocumentOptionsDto options = new SurveyDocumentOptionsDto(rootEntityType, rootEntityReference);
		options.setRecipientEmail(person.getEmailAddress(true));

		form.setValue(options);
		CommitDiscardWrapperComponent<SurveyDocumentOptionsForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getCaption(Captions.surveySend));

		editView.addCommitListener(() -> {
			SurveyDocumentOptionsDto surveyOptions = form.getValue();
			try {
				FacadeProvider.getSurveyFacade().sendDocument(surveyOptions);
				callback.run();
			} catch (DocumentTemplateException | ValidationException e) {
				new Notification(
					String.format(I18nProperties.getString(Strings.errorDocumentGeneration), surveyOptions.getSurvey().getCaption()),
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			} catch (AttachmentException | IOException | ExternalEmailException e) {
				new Notification(
					I18nProperties.getString(Strings.headingErrorSendingExternalEmail),
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		});
		form.setSurveyErrorCallback(inError -> {
			editView.getCommitButton().setEnabled(!inError);
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingSurveySendDocument));
	}

	public void generateSurveyDocument(RootEntityType rootEntityType, ReferenceDto rootEntityReference, Disease disease, Runnable callback) {
		SurveyDocumentOptionsForm form = new SurveyDocumentOptionsForm(disease, false, null);
		form.setWidth(600, Sizeable.Unit.PIXELS);
		form.setValue(new SurveyDocumentOptionsDto(rootEntityType, rootEntityReference));

		CommitDiscardWrapperComponent<SurveyDocumentOptionsForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getCaption(Captions.surveyGenerate));

		editView.addCommitListener(() -> {
			SurveyDocumentOptionsDto surveyOptions = form.getValue();
			try {
				FacadeProvider.getSurveyFacade().generateDocument(surveyOptions);
				callback.run();
			} catch (DocumentTemplateException | ValidationException e) {
				new Notification(
					String.format(I18nProperties.getString(Strings.errorDocumentGeneration), surveyOptions.getSurvey().getCaption()),
					e.getMessage(),
					e instanceof ValidationException ? Notification.Type.WARNING_MESSAGE : Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		});

		form.getSurveyCombo().addValueChangeListener(e -> {
			SurveyDto survey = (SurveyDto) e.getProperty().getValue();
			editView.getCommitButton().setEnabled(survey.getDocumentTemplate() != null);
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingSurveyGenerateDocument));
	}

	private static class SurveyDocumentOptionsForm extends AbstractEditForm<SurveyDocumentOptionsDto> {

		private static final long serialVersionUID = 360077311341123575L;

		private static final String SURVEY_WARNING_LOC = "surveyWarningLoc";
		private static final String TEMPLATE_ADDITIONAL_VARIABLES_LOC = "templateAdditionalVariablesLoc";

		private static final String HTML_LAYOUT = fluidRowLocs(SurveyDocumentOptionsDto.RECIPIENT_EMAIL)
			+ fluidRowLocs(SurveyDocumentOptionsDto.SURVEY)
			+ fluidRowLocs(SURVEY_WARNING_LOC)
			+ fluidRowLocs(TEMPLATE_ADDITIONAL_VARIABLES_LOC);

		private final Disease disease;
		private final boolean forEmail;
		private final PersonDto person;

		private VerticalLayout additionalVariablesComponent;
		private Consumer<Boolean> surveyErrorCallback;

		protected SurveyDocumentOptionsForm(Disease disease, boolean forEmail, PersonDto person) {
			super(SurveyDocumentOptionsDto.class, SurveyDocumentOptionsDto.I18N_PREFIX, false);
			this.disease = disease;
			this.forEmail = forEmail;
			this.person = person;
			this.surveyErrorCallback = e -> {};

			addFields();
			hideValidationUntilNextCommit();
		}

		@Override
		protected String createHtmlLayout() {
			return HTML_LAYOUT;
		}

		@Override
		protected void addFields() {
			if (forEmail) {
				ComboBox recipientEmailCombo = addField(SurveyDocumentOptionsDto.RECIPIENT_EMAIL, ComboBox.class);
				recipientEmailCombo.setRequired(true);
				List<String> recipientEmails = person.getAllEmailAddresses();
				FieldHelper.updateItems(recipientEmailCombo, recipientEmails);
				String primaryEmailAddress = person.getEmailAddress(true);
				if (StringUtils.isNotBlank(primaryEmailAddress)) {
					recipientEmailCombo
						.setItemCaption(primaryEmailAddress, primaryEmailAddress + " (" + I18nProperties.getCaption(Captions.primarySuffix) + ")");
				}
			}

			ComboBox surveyCombo = addCustomField(SurveyDocumentOptionsDto.SURVEY, SurveyDto.class, ComboBox.class);
			surveyCombo.setRequired(true);
			List<SurveyDto> surveys = FacadeProvider.getSurveyFacade().getAllByDisease(disease);
			FieldHelper.updateItems(surveyCombo, surveys);
			surveys.forEach(survey -> surveyCombo.setItemCaption(survey, survey.getName()));

			Label surveyWarningLabel = new Label();
			surveyWarningLabel.addStyleName(CssStyles.LABEL_WARNING);
			surveyWarningLabel.setVisible(false);
			getContent().addComponent(surveyWarningLabel, SURVEY_WARNING_LOC);

			additionalVariablesComponent = new VerticalLayout();
			additionalVariablesComponent.setSpacing(false);
			additionalVariablesComponent.setMargin(new MarginInfo(false, false, true, false));
			getContent().addComponent(additionalVariablesComponent, TEMPLATE_ADDITIONAL_VARIABLES_LOC);

			surveyCombo.addValueChangeListener(e -> {
				SurveyDto survey = (SurveyDto) e.getProperty().getValue();
				additionalVariablesComponent.removeAllComponents();
				surveyErrorCallback.accept(false);
				surveyWarningLabel.setVisible(false);

				if(survey == null) {
					return;
				}

				if (survey.getDocumentTemplate() == null) {
					surveyWarningLabel.setVisible(true);
					surveyWarningLabel.setValue(I18nProperties.getString(Strings.messageSurveyNoDocumentTemplate));
					surveyErrorCallback.accept(true);
					return;
				}

				if (forEmail && survey.getEmailTemplate() == null) {
					surveyWarningLabel.setVisible(true);
					surveyWarningLabel.setValue(I18nProperties.getString(Strings.messageSurveyNoEmailTemplate));
					surveyErrorCallback.accept(true);
					return;
				}

				if(!FacadeProvider.getSurveyFacade().hasUnassignedTokens(survey.toReference())) {
					surveyWarningLabel.setVisible(true);
					surveyWarningLabel.setValue(I18nProperties.getString(Strings.messageSurveyNoTokens));
					surveyErrorCallback.accept(true);
					return;
				}

				try {
					DocumentVariables documentVariables =
						FacadeProvider.getDocumentTemplateFacade().getDocumentVariables(survey.getDocumentTemplate());
					List<String> additionalVariables = documentVariables.getAdditionalVariables();
					if (additionalVariables != null && !additionalVariables.isEmpty()) {
						for (String variable : additionalVariables) {
							TextField variableInput = new TextField(variable);
							variableInput.setWidth(100F, Unit.PERCENTAGE);
							additionalVariablesComponent.addComponent(variableInput);
						}
					}
				} catch (DocumentTemplateException ex) {
					LoggerFactory.getLogger(getClass()).error("Error while reading document variables.", e);
					new Notification(I18nProperties.getString(Strings.errorOccurred), ex.getMessage(), Notification.Type.ERROR_MESSAGE, false)
						.show(Page.getCurrent());
				}
			});
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

		public ComboBox getSurveyCombo() {
			return getField(SurveyDocumentOptionsDto.SURVEY);
		}

		@Override
		public SurveyDocumentOptionsDto getValue() {
			SurveyDocumentOptionsDto options = super.getValue();
			options.setSurvey(((SurveyDto) getSurveyCombo().getValue()).toReference());
			options.setTemplateProperties(readAdditionalVariables());

			return options;
		}

		public void setSurveyErrorCallback(Consumer<Boolean> callback) {
			surveyErrorCallback = callback;
		}
	}
}
