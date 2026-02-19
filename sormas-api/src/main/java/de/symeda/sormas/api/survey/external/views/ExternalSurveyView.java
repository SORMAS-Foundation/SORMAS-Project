package de.symeda.sormas.api.survey.external.views;

import java.util.List;

/**
 * View used to display an external Survey in an tool-agnostic manner.
 */
public class ExternalSurveyView {

	private List<QuestionAnswersView> questionAnswersViews;

	public List<QuestionAnswersView> getQuestionAnswersViews() {
		return questionAnswersViews;
	}

	public ExternalSurveyView setQuestionAnswersViews(List<QuestionAnswersView> questionAnswersViews) {
		this.questionAnswersViews = questionAnswersViews;
		return this;
	}

	@Override
	public String toString() {
		return "ExternalSurveyView{" + "questionAnswersViews=" + questionAnswersViews + '}';
	}
}
