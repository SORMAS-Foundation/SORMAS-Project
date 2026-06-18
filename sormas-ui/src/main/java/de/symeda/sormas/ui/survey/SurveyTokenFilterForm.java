package de.symeda.sormas.ui.survey;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class SurveyTokenFilterForm extends AbstractFilterForm<SurveyTokenCriteria> {

	public SurveyTokenFilterForm() {
		super(SurveyTokenCriteria.class, SurveyTokenDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			SurveyTokenCriteria.FREE_TEXT,
			SurveyTokenCriteria.RESPONSE_RECEIVED,
			SurveyTokenCriteria.TOKEN_NOT_ASSIGNED };
	}

	@Override
	protected void addFields() {
		UserDto user = currentUserDto();

		addField(
			FieldConfiguration
				.withCaptionAndPixelSized(SurveyCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptSurveyTokenFreeTextSearch), 200),
			TextField.class);

		addField(
			getContent(),
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				SurveyTokenCriteria.RESPONSE_RECEIVED,
				I18nProperties.getCaption(Captions.surveyTokenFilterResponseReceived),
				null,
				CssStyles.CHECKBOX_FILTER_INLINE));

		addField(
			getContent(),
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				SurveyTokenCriteria.TOKEN_NOT_ASSIGNED,
				I18nProperties.getCaption(Captions.surveyTokenFilterTokenNotAssigned),
				null,
				CssStyles.CHECKBOX_FILTER_INLINE));

	}
}
