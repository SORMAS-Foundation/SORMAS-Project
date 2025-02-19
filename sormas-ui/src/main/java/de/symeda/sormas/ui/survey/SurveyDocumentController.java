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

import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyDocumentOptionsDto;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveyDocumentController {

	public void generateSurveyDocument(RootEntityType rootEntityType, ReferenceDto rootEntityReference, Disease disease, Runnable callback) {
		SurveyDocumentOptionsForm form = new SurveyDocumentOptionsForm(disease);
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

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingSurveyGenerateDocument));
	}

	private static class SurveyDocumentOptionsForm extends AbstractEditForm<SurveyDocumentOptionsDto> {

		private static final long serialVersionUID = 360077311341123575L;

		private static final String TEMPLATE_ADDITIONAL_VARIABLES_LOC = "templateAdditionalVariablesLoc";

		private static final String HTML_LAYOUT = fluidRowLocs(SurveyDocumentOptionsDto.SURVEY) + fluidRowLocs(TEMPLATE_ADDITIONAL_VARIABLES_LOC);

		private final Disease disease;

		private VerticalLayout additionalVariablesComponent;

		protected SurveyDocumentOptionsForm(Disease disease) {
			super(SurveyDocumentOptionsDto.class, SurveyDocumentOptionsDto.I18N_PREFIX, false);
			this.disease = disease;
			addFields();
			hideValidationUntilNextCommit();
		}

		@Override
		protected String createHtmlLayout() {
			return HTML_LAYOUT;
		}

		@Override
		protected void addFields() {
			ComboBox surveyCombo = addCustomField(SurveyDocumentOptionsDto.SURVEY, SurveyDto.class, ComboBox.class);
			surveyCombo.setRequired(true);
			List<SurveyDto> surveys = FacadeProvider.getSurveyFacade().getAllByDisease(disease);
			FieldHelper.updateItems(surveyCombo, surveys);
			surveys.forEach(survey -> surveyCombo.setItemCaption(survey, survey.getName()));

			additionalVariablesComponent = new VerticalLayout();
			additionalVariablesComponent.setSpacing(false);
			additionalVariablesComponent.setMargin(new MarginInfo(false, false, true, false));
			getContent().addComponent(additionalVariablesComponent, TEMPLATE_ADDITIONAL_VARIABLES_LOC);

			surveyCombo.addValueChangeListener(e -> {
				SurveyDto survey = (SurveyDto) e.getProperty().getValue();
				additionalVariablesComponent.removeAllComponents();
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
	}
}
