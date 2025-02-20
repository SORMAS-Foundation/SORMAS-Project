package de.symeda.sormas.ui.survey;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.ui.configuration.docgeneration.DocumentTemplateSection;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SurveyDataForm extends AbstractEditForm<SurveyDto> {

	private static final String SURVEY_DOCUMENT_SECTION = "surveyDocumentSection";

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRowLocs(SurveyDto.SURVEY_NAME, "") + fluidRowLocs(SurveyDto.DISEASE, "")
			+ fluidRowLocs(SURVEY_DOCUMENT_SECTION, "");
	//@formatter:on

	private VerticalLayout gridLayout;
	private SurveyReferenceDto surveyReference;

	public SurveyDataForm(boolean isEditAllowed, SurveyReferenceDto surveyReference) {
		super(SurveyDto.class, SurveyDto.I18N_PREFIX, false, null, null, isEditAllowed);
		this.surveyReference = surveyReference;
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

		gridLayout = new VerticalLayout(
			new DocumentTemplateSection(
				new DocumentTemplateCriteria(DocumentWorkflow.SURVEY_DOCUMENT, null, surveyReference),
				false,
				new SurveyDocumentTemplateReceiver(DocumentWorkflow.SURVEY_DOCUMENT, surveyReference)),
			new DocumentTemplateSection(
				new DocumentTemplateCriteria(DocumentWorkflow.SURVEY_EMAIL, null, surveyReference),
				false,
				new SurveyEmailTemplateReceiver(DocumentWorkflow.SURVEY_EMAIL, surveyReference)));

		gridLayout.setWidth(100, Unit.PERCENTAGE);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("crud-main-layout");

		getContent().addComponent(gridLayout, SURVEY_DOCUMENT_SECTION);

		setRequired(true, SurveyDto.SURVEY_NAME, SurveyDto.DISEASE);
	}
}
