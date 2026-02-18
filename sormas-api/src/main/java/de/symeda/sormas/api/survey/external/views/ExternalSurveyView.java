package de.symeda.sormas.api.survey.external.views;

import java.util.List;

/**
 * View used to display
 */
public class ExternalSurveyView {

	private List<QuestionAnswersView> questionAnswersViews;

	ExternalSurveyView(List<QuestionAnswersView> questionAnswersViews) {
		this.questionAnswersViews = questionAnswersViews;
	}

	public static SurveyViewBuilder builder() {
		return new SurveyViewBuilder();
	}

	public List<QuestionAnswersView> getQuestionAnswersViews() {
		return this.questionAnswersViews;
	}

	public void setQuestionAnswersViews(List<QuestionAnswersView> questionAnswersViews) {
		this.questionAnswersViews = questionAnswersViews;
	}

	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ExternalSurveyView))
			return false;
		final ExternalSurveyView other = (ExternalSurveyView) o;
		if (!other.canEqual((Object) this))
			return false;
		final Object this$questionAnswersViews = this.getQuestionAnswersViews();
		final Object other$questionAnswersViews = other.getQuestionAnswersViews();
		if (this$questionAnswersViews == null ? other$questionAnswersViews != null : !this$questionAnswersViews.equals(other$questionAnswersViews))
			return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof ExternalSurveyView;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $questionAnswersViews = this.getQuestionAnswersViews();
		result = result * PRIME + ($questionAnswersViews == null ? 43 : $questionAnswersViews.hashCode());
		return result;
	}

	public String toString() {
		return "SurveyView(questionAnswersViews=" + this.getQuestionAnswersViews() + ")";
	}

	public static class SurveyViewBuilder {

		private List<QuestionAnswersView> questionAnswersViews;

		SurveyViewBuilder() {
		}

		public SurveyViewBuilder questionAnswersViews(List<QuestionAnswersView> questionAnswersViews) {
			this.questionAnswersViews = questionAnswersViews;
			return this;
		}

		public ExternalSurveyView build() {
			return new ExternalSurveyView(this.questionAnswersViews);
		}

		public String toString() {
			return "SurveyView.SurveyViewBuilder(questionAnswersViews=" + this.questionAnswersViews + ")";
		}
	}
}
