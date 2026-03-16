package de.symeda.sormas.api.survey.external.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class QuestionAnswersView {

	@NotNull
	private String question;

	@Nullable
	private String answer;

	@JsonIgnore
	@Nullable
	private String answerText;

	/**
	 * Some questions can be grouped together.
	 * Example: PersonalInfo:
	 * - PhoneNumber
	 * - Email
	 */
	private List<QuestionAnswersView> subquestions = new ArrayList<>();

	public String getQuestion() {
		return question;
	}

	public QuestionAnswersView setQuestion(String question) {
		this.question = question;
		return this;
	}

	public String getAnswer() {
		return answer;
	}

	public QuestionAnswersView setAnswer(String answer) {
		this.answer = answer;
		return this;
	}

	public List<QuestionAnswersView> getSubquestions() {
		return subquestions;
	}

	public QuestionAnswersView setSubquestions(List<QuestionAnswersView> subquestions) {
		this.subquestions = subquestions;
		return this;
	}

	@Nullable
	public String getAnswerText() {
		return answerText;
	}

	public QuestionAnswersView setAnswerText(@Nullable String answerText) {
		this.answerText = answerText;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		QuestionAnswersView that = (QuestionAnswersView) o;
		return Objects.equals(question, that.question)
			&& Objects.equals(answer, that.answer)
			&& Objects.equals(answerText, that.answerText)
			&& Objects.equals(subquestions, that.subquestions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(question, answer, answerText, subquestions);
	}

	@Override
	public String toString() {
		return "QuestionAnswersView{" + "question='" + question + '\'' + ", answer='" + answer + '\'' + ", answerText='" + answerText + '\''
			+ ", subquestions=" + subquestions + '}';
	}
}
