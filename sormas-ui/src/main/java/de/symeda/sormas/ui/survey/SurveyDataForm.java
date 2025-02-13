package de.symeda.sormas.ui.survey;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SurveyDataForm extends AbstractEditForm<SurveyDto> {

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRowLocs(SurveyDto.SURVEY_NAME, "") + fluidRowLocs(SurveyDto.DISEASE, "");
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
		addDiseaseField(SurveyDto.DISEASE, false, true, false);

		addField(SurveyDto.SURVEY_NAME, TextField.class);

		setRequired(true, SurveyDto.SURVEY_NAME, SurveyDto.DISEASE);
	}
}
