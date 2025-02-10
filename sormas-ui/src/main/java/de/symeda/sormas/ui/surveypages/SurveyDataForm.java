package de.symeda.sormas.ui.surveypages;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SurveyDataForm extends AbstractEditForm<SurveyDto> {

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRowLocs(3, SurveyDto.CREATION_DATE)
			+ fluidRowLocs(SurveyDto.DISEASE, "") + fluidRowLocs(SurveyDto.SURVEY_NAME, "");
	//@formatter:on

	public SurveyDataForm(boolean isEditAllowed) {
		super(SurveyDto.class, SurveyDto.I18N_PREFIX, false, null, null, isEditAllowed);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(SurveyDto.CREATION_DATE, DateField.class);
		addDiseaseField(SurveyDto.DISEASE, false, true, false);

		addField(SurveyDto.SURVEY_NAME, TextField.class);

		setRequired(true, SurveyDto.CREATION_DATE, SurveyDto.SURVEY_NAME, SurveyDto.DISEASE);
	}
}
