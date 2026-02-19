package de.symeda.sormas.api.survey.external.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

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

	@Override
	public String toString() {
		return "QuestionAnswersView{" + "question='" + question + '\'' + ", answer='" + answer + '\'' + ", subquestions=" + subquestions + '}';
	}
}
