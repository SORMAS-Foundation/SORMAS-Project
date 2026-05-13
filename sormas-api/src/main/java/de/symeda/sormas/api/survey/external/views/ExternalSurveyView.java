package de.symeda.sormas.api.survey.external.views;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * View used to display an external Survey in an tool-agnostic manner.
 */
@AuditedClass
public class ExternalSurveyView implements Serializable {

	private static final long serialVersionUID = 1448651469231018412L;

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
