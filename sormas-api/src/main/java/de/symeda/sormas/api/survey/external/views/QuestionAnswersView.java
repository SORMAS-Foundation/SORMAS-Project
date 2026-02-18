package de.symeda.sormas.api.survey.external.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionAnswersView {

	@NotNull
	private String question;

	@Nullable
	private String answer;

	/**
	 * Some questions can be grouped together.
	 * Example: PersonalInfo:
	 * - PhoneNumber
	 * - Email
	 */
	private List<QuestionAnswersView> subquestions = new ArrayList<>();

	public QuestionAnswersView(@NotNull String question, @Nullable String answer, List<QuestionAnswersView> subquestions) {
		this.question = question;
		this.answer = answer;
		this.subquestions = subquestions;
	}

	public QuestionAnswersView() {
	}

	private static List<QuestionAnswersView> $default$subquestions() {
		return new ArrayList<>();
	}

	public static QuestionAnswersViewBuilder builder() {
		return new QuestionAnswersViewBuilder();
	}

	public String getQuestion() {
		return Optional.ofNullable(question).filter(StringUtils::isNotBlank).orElse(answer);
	}

	@Nullable
	public String getAnswer() {
		if (CollectionUtils.isNotEmpty(subquestions)) {
			return answer;
		}

		return Optional.ofNullable(answer).filter(StringUtils::isNotBlank).orElse(question);
	}

	@JsonProperty("singleIdentifier")
	public boolean singleIdentifier() {
		return StringUtils.equals(getQuestion(), getAnswer());
	}

	public List<QuestionAnswersView> getSubquestions() {
		return this.subquestions;
	}

	public void setQuestion(@NotNull String question) {
		this.question = question;
	}

	public void setAnswer(@Nullable String answer) {
		this.answer = answer;
	}

	public void setSubquestions(List<QuestionAnswersView> subquestions) {
		this.subquestions = subquestions;
	}

	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof QuestionAnswersView))
			return false;
		final QuestionAnswersView other = (QuestionAnswersView) o;
		if (!other.canEqual((Object) this))
			return false;
		final Object this$question = this.getQuestion();
		final Object other$question = other.getQuestion();
		if (this$question == null ? other$question != null : !this$question.equals(other$question))
			return false;
		final Object this$answer = this.getAnswer();
		final Object other$answer = other.getAnswer();
		if (this$answer == null ? other$answer != null : !this$answer.equals(other$answer))
			return false;
		final Object this$subquestions = this.getSubquestions();
		final Object other$subquestions = other.getSubquestions();
		if (this$subquestions == null ? other$subquestions != null : !this$subquestions.equals(other$subquestions))
			return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof QuestionAnswersView;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $question = this.getQuestion();
		result = result * PRIME + ($question == null ? 43 : $question.hashCode());
		final Object $answer = this.getAnswer();
		result = result * PRIME + ($answer == null ? 43 : $answer.hashCode());
		final Object $subquestions = this.getSubquestions();
		result = result * PRIME + ($subquestions == null ? 43 : $subquestions.hashCode());
		return result;
	}

	public String toString() {
		return "QuestionAnswersView(question=" + this.getQuestion() + ", answer=" + this.getAnswer() + ", subquestions=" + this.getSubquestions()
			+ ")";
	}

	public static class QuestionAnswersViewBuilder {

		private @NotNull String question;
		private String answer;
		private List<QuestionAnswersView> subquestions$value;
		private boolean subquestions$set;

		QuestionAnswersViewBuilder() {
		}

		public QuestionAnswersViewBuilder question(@NotNull String question) {
			this.question = question;
			return this;
		}

		public QuestionAnswersViewBuilder answer(@Nullable String answer) {
			this.answer = answer;
			return this;
		}

		public QuestionAnswersViewBuilder subquestions(List<QuestionAnswersView> subquestions) {
			this.subquestions$value = subquestions;
			this.subquestions$set = true;
			return this;
		}

		public QuestionAnswersView build() {
			List<QuestionAnswersView> subquestions$value = this.subquestions$value;
			if (!this.subquestions$set) {
				subquestions$value = QuestionAnswersView.$default$subquestions();
			}
			return new QuestionAnswersView(this.question, this.answer, subquestions$value);
		}

		public String toString() {
			return "QuestionAnswersView.QuestionAnswersViewBuilder(question=" + this.question + ", answer=" + this.answer + ", subquestions$value="
				+ this.subquestions$value + ")";
		}
	}
}
